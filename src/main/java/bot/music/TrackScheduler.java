package bot.music;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.rest.util.Color;

public final class TrackScheduler implements CustomAudioLoadResultHandler {

    private final GuildMusicPlayer guildMusicPlayer;

    public TrackScheduler(final GuildMusicPlayer player) {
        this.guildMusicPlayer = player;
    }

    @Override
    public void trackLoaded(AudioTrack track, int index) {
        guildMusicPlayer.addToQueue(index, track);
        guildMusicPlayer.createEmbed(Color.GREEN, "Added track \"" + track.getInfo().title + "\" to the queue");
        guildMusicPlayer.startPlay();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist, int index) {
        guildMusicPlayer.addToQueue(index, playlist.getTracks());
        guildMusicPlayer.createEmbed("Added playlist " + playlist.getName() + " to the queue");
        guildMusicPlayer.startPlay();
    }

    @Override
    public void noMatches() {
        guildMusicPlayer.createEmbed("No Matches found");
    }

    @Override
    public void loadFailed(final FriendlyException exception) {
        guildMusicPlayer.createEmbed("Load Failed");
    }
}