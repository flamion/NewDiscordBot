package bot.music;

import bot.TimeFormatter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class GuildMusicPlayer {

    private static final Pattern onlyNumbersPattern = Pattern.compile("\\d+");
    private final List<AudioTrack> queue = new LinkedList<>();
    private final TrackScheduler scheduler = new TrackScheduler(this);
    private final Autoplay autoplay = new Autoplay(this);
    private final CustomPlayerManager playerManager = new CustomPlayerManager();
    private final AudioPlayer player = playerManager.createPlayer();
    private final AudioProvider provider = new LavaPlayerAudioProvider(player);
    private Mono<VoiceConnection> voiceConnection;
    private Mono<MessageChannel> channel;
    private boolean loop = false;


    /**
     * @param channel the message channel the bot should use. It in the constructor since the bot sends a message when it first joins
     */
    public GuildMusicPlayer(Mono<MessageChannel> channel) {
        this.channel = channel;
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
        player.addListener(autoplay);
        createEmbed(Color.GREEN, "Joining your channel");
    }

    /**
     * @param connection Changes the currently active voice connection that is assigned to the player.
     *                   If there is a currently active voice connection it will get terminated
     *                   In the Future this will be migrated to the Voice connection registry
     */
    public void setVoiceConnection(Mono<VoiceConnection> connection) {
        if (this.voiceConnection != null) {
            voiceConnection.flatMap(VoiceConnection::disconnect).subscribe();
        }
        this.voiceConnection = connection;
    }

    /**
     * @param channel Sets the currently used Text Channel.
     *                This is supposed to be updated every time the bot receives a new message that is relevant to the {@link GuildMusicPlayer}
     */
    public void setMessageChannel(Mono<MessageChannel> channel) {
        this.channel = channel;
    }

    public AudioProvider getProvider() {
        return provider;
    }

    public boolean loopActive() {
        return loop;
    }

    public void switchLoopState() {
        loop = !loop;
        if (loop) {
            createEmbed("Loop enabled");
        } else {
            createEmbed("Loop disabled");
        }
    }

    public void playTrack(AudioTrack track) {
        player.playTrack(track);
    }

    public void addToQueue(int index, List<AudioTrack> tracks) {
        if (index <= queue.size() && index >= 0) {
            queue.addAll(index, tracks);
        } else {
            queue.addAll(tracks);
        }
    }

    public void addToQueue(int index, AudioTrack track) {
        if (index <= queue.size() && index >= 0) {
            queue.add(index, track);
        } else {
            queue.add(track);
        }
    }

    public void listQueue(String content) {
        StringBuilder builder = new StringBuilder();
        try {
            if (queue.size() == 0) {
                builder.append("Queue is empty");
            }
            String[] split = content.split(" ");
            int page;
            if (split.length < 2) {
                page = 1;
            } else {
                page = Math.max(Integer.parseInt(split[1]), 1);
            }
            for (int i = 15 * (page - 1); i < 15 * page; i++) {
                AudioTrack track = queue.get(i);
                builder.append(i);
                builder.append(". ");
                builder.append(track.getInfo().title);
                builder.append("\n");
            }
            builder.append("\nPage ").append(page).append(" of ").append((queue.size() / 15));
        } catch (IndexOutOfBoundsException ignore) {

        } catch (NumberFormatException e) {
            createEmbed(Color.RED, "Incorrect Arguments");
        }
        createEmbed(builder.toString());
    }

    public void clearQueue() {
        queue.clear();
        createEmbed("Cleared queue");
    }

    public void pause() {
        player.setPaused(!player.isPaused());
        if (player.isPaused()) {
            createEmbed("Paused player");
        } else {
            createEmbed("Unpaused player");
        }
    }

    public void resume() {
        player.setPaused(false);
        createEmbed("Resumed player");
    }

    public void setPosition(long millis) {
        if (player.getPlayingTrack() != null && millis < player.getPlayingTrack().getDuration() && millis >= 0) {
            player.getPlayingTrack().setPosition(millis);
        }
    }

    public void stopTrack() {
        player.stopTrack();
    }

    public void startPlay() {
        if (player.getPlayingTrack() == null && queue.size() > 0) {
            player.playTrack(queue.get(0));
            queue.remove(0);
        }
    }

    public void skip() {
        if (player.getPlayingTrack() != null) {
            player.stopTrack();
            startPlay();
            createEmbed("Track skipped");
        } else {
            createEmbed("No Track playing");
        }
    }

    private String infoCurrentSong() {
        StringBuilder builder = new StringBuilder();
        if (player.getPlayingTrack() != null) {
            builder.append("Title: ").append(player.getPlayingTrack().getInfo().title).append("\n");
            builder.append("Link: <").append(player.getPlayingTrack().getInfo().uri).append(">").append(" \n");
            builder.append(TimeFormatter.generalTimeFormatter("Duration: ", player.getPlayingTrack().getDuration()));
            builder.append(TimeFormatter.generalTimeFormatter("Duration left: ", player.getPlayingTrack().getDuration() - player.getPlayingTrack().getPosition()));
        } else {
            builder.append("No song is currently playing");
        }
        if (loop) {
            builder.append("\nLoop Enabled");
        } else {
            builder.append("\nLoop disabled");
        }
        return builder.toString();
    }

    private String infoOfIndex(int index) throws IndexOutOfBoundsException {
        AudioTrack track = queue.get(index);
        StringBuilder builder = new StringBuilder();
        builder.append("Title: ").append(track.getInfo().title).append("\n");
        builder.append("Link: <").append(track.getInfo().uri).append(">").append(" \n");
        builder.append(TimeFormatter.generalTimeFormatter("Duration: ", track.getDuration()));
        if (loop) {
            builder.append("\nLoop Enabled");
        } else {
            builder.append("\nLoop disabled");
        }
        return builder.toString();
    }

    public void info(String content) {
        try {
            String[] split = content.split(" ");
            if (split.length < 2) {
                createEmbed(infoCurrentSong());
            } else {
                createEmbed(infoOfIndex(Integer.parseInt(split[1])));
            }
        } catch (NumberFormatException e) {
            createEmbed("Invalid Arguments");
        } catch (IndexOutOfBoundsException e) {
            createEmbed("Specified track not in queue");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        voiceConnection.flatMap(VoiceConnection::disconnect).subscribe();
    }

    /**
     * @param content Message Content
     *                <p>
     *                Adds a track to the end of the queue
     */
    public void playLink(String content) {
        if (isValidLink(content)) {
            playerManager.loadItem(safeArgumentSplit(content), scheduler, -1);
        } else {
            createEmbed(Color.RED, "Invalid Link Provided");
        }
    }

    /**
     * @param content Message Content
     *                <p>
     *                Adds a track at place 0 to the queue so it will play next
     */
    public void next(String content) {
        if (isValidLink(content)) {
            playerManager.loadItem(safeArgumentSplit(content), scheduler, 0);
        } else {
            createEmbed(Color.RED, "Invalid Link Provided");
        }
    }

    /**
     * @param index1 Index of first track to be swapped
     * @param index2 Index of second track to be swapped
     *               <p>
     *               Swaps two tracks in the queue and checks whether they would even work
     */
    public void swap(int index1, int index2) {
        if (index1 >= 0 && index2 >= 0) {
            if (index1 < queue.size() && index2 < queue.size()) {
                Collections.swap(queue, index1, index2);
            }
        }
    }

    /**
     * @param content Messagecontent containing a link and position in queue, for example -insert [link] [place in queue]
     */
    public void insert(String content) {
        String[] split = content.split(" ");
        if (split.length > 2 && isValidLink(split[1]) && onlyNumbersPattern.matcher(split[2]).find()) {
            playerManager.loadItem(split[1], scheduler, Integer.parseInt(split[2]));
        } else {
            createEmbed(Color.RED, "Wrong arguments given");
        }
    }

    public void createEmbed(String text) {
        channel.flatMap(messageChannel -> messageChannel.createEmbed(spec -> spec.setColor(Color.CYAN).setDescription(text))).subscribe();
    }

    public void createEmbed(Color color, String text) {
        channel.flatMap(messageChannel -> messageChannel.createEmbed(spec -> spec.setColor(color).setDescription(text))).subscribe();
    }

    private boolean isValidLink(String link) {
        return link.toLowerCase().contains("youtube") || link.toLowerCase().contains("youtu.be");
    }

    /**
     * @param content used to split a message into its contents and returning the second word (Which could be a link or an argument)
     * @return returns the second word of the input, like an argument or a link for certain commands. returns an empty string to avoid the usage of null
     */
    private String safeArgumentSplit(String content) {
        String[] split = content.split(" ");
//        if (split.length > 1) {
//            return split[1];
//        }
//        return "";
        return split.length > 1 ? split[1] : "";
    }
}
