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


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GuildMusicPlayer {

    private Mono<VoiceConnection> voiceConnection;
    private Mono<MessageChannel> channel;
    private final List<AudioTrack> queue = new LinkedList<>();
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private final AudioPlayer player = playerManager.createPlayer();
    private AudioProvider provider = new LavaPlayerAudioProvider(player);
    private boolean loop = false;

    public GuildMusicPlayer(Mono<MessageChannel> channel) {
        this.channel = channel;
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public void setVoiceConnection(Mono<VoiceConnection> connection) {
        this.voiceConnection = connection;
    }

    public AudioProvider getProvider() {
        return provider;
    }

    public boolean loopActive() {
        return loop;
    }

    public void switchLoopState() {
        loop = !loop;
    }

    public void playTrack(AudioTrack track) {
        player.playTrack(track);
    }

    public void addToQueue(AudioTrack track) {
        queue.add(track);
    }

    public void addToQueue(int position, AudioTrack track) {
        queue.add(position, track);
    }

    public void stopTrack() {
        player.stopTrack();
    }

    public void createEmbed(String text) {
        channel.flatMap(messageChannel -> messageChannel.createEmbed(spec -> spec.setColor(Color.CYAN).setDescription(text))).subscribe();
    }

    public void createEmbed(Color color, String text) {
        channel.flatMap(messageChannel -> messageChannel.createEmbed(spec -> spec.setColor(color).setDescription(text))).subscribe();
    }
}
