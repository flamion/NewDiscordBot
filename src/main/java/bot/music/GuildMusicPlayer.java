package bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Mono;

import javax.sound.midi.Track;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GuildMusicPlayer {

    private Mono<VoiceConnection> voiceConnection;
    private Mono<MessageChannel> channel;
    private final List<AudioTrack> queue = new LinkedList<>();
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private final AudioPlayer player = playerManager.createPlayer();
    private AudioProvider provider = new LavaPlayerAudioProvider(player);
    private final TrackScheduler scheduler = new TrackScheduler(this);
    private final Autoplay autoplay = new Autoplay(this);

    private boolean loop = false;


    public GuildMusicPlayer(Mono<MessageChannel> channel) {
        this.channel = channel;
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
        player.addListener(autoplay);
    }

    public void setVoiceConnection(Mono<VoiceConnection> connection) {
        this.voiceConnection = connection;
    }

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

    public void addToQueue(AudioTrack track) {
        queue.add(track);
    }

    public void addToQueue(List<AudioTrack> tracks) {
        queue.addAll(tracks);
    }

    public void addToQueue(int position, AudioTrack track) {
        queue.add(position, track);
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

    public void disconnect() {
        voiceConnection.flatMap(VoiceConnection::disconnect).subscribe();
    }

    public void playLink(String content) {
        if (isValidLink(content)) {
            createEmbed(Color.GREEN, "Link Valid");
            playerManager.loadItem(safeSplit(content), scheduler);
        } else {
            createEmbed(Color.RED, "Invalid Link Provided");
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

    private String safeSplit(String content) {
        String[] split = content.split(" ");
        if (split.length > 1) {
            return split[1];
        }
        return "";
    }

    private static void toConsole(String toConsole) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(formatter.format(date) + " " + toConsole);
    }
}
