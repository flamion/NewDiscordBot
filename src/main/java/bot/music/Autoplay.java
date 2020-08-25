package bot.music;

import bot.BotMain;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import discord4j.rest.util.Color;


public final class Autoplay extends AudioEventAdapter {

    private final GuildMusicPlayer guildMusicPlayer;

    public Autoplay(GuildMusicPlayer player) {
        this.guildMusicPlayer = player;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        guildMusicPlayer.createEmbed(Color.GREEN, "Player paused");
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        guildMusicPlayer.createEmbed(Color.GREEN, "Player resumed playing");
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        guildMusicPlayer.createEmbed("Now playing: \n" +
                "Name: " + track.getInfo().title);

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (guildMusicPlayer.loopActive()) {
            guildMusicPlayer.playTrack(track.makeClone());
            return;
        }
        if (endReason.mayStartNext) {
            guildMusicPlayer.startPlay();
        }
        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
        //musicPlayer.createEmbed("There was an error with the track " + track.getInfo().title);
        BotMain.toConsole("There was an error with the track " + track.getInfo().title);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        guildMusicPlayer.addToQueue(0, track.makeClone());
        guildMusicPlayer.stopTrack();
        guildMusicPlayer.createEmbed(Color.RED, "The track got stuck. Restarting Track");
    }
}
