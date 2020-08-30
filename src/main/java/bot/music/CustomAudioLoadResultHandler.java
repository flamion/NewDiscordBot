package bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface CustomAudioLoadResultHandler {

    /**
     * Called when the requested item is a track and it was successfully loaded.
     * @param track The loaded track
     */
    void trackLoaded(AudioTrack track, int index);

    /**
     * Called when the requested item is a playlist and it was successfully loaded.
     * @param playlist The loaded playlist
     */
    void playlistLoaded(AudioPlaylist playlist, int index);

    /**
     * Called when there were no items found by the specified identifier.
     */
    void noMatches();

    /**
     * Called when loading an item failed with an exception.
     * @param exception The exception that was thrown
     */
    void loadFailed(FriendlyException exception);
}
