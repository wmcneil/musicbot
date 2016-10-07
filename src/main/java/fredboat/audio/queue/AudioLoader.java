package fredboat.audio.queue;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.GuildPlayer;
import fredboat.util.TextUtils;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.LoggerFactory;

public class AudioLoader implements AudioLoadResultHandler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AudioLoader.class);

    private final ITrackProvider trackProvider;
    private final AudioPlayerManager playerManager;
    private final GuildPlayer gplayer;
    private final ConcurrentLinkedQueue<IdentifierContext> identifierQueue = new ConcurrentLinkedQueue();
    private IdentifierContext context = null;
    private volatile boolean isLoading = false;

    public AudioLoader(ITrackProvider trackProvider, AudioPlayerManager playerManager, GuildPlayer gplayer) {
        this.trackProvider = trackProvider;
        this.playerManager = playerManager;
        this.gplayer = gplayer;
    }

    public void loadAsync(IdentifierContext ic) {
        identifierQueue.add(ic);
        if (!isLoading) {
            loadNextAsync();
        }
    }

    private void loadNextAsync() {
        IdentifierContext ic = identifierQueue.poll();
        if (ic != null) {
            isLoading = true;
            context = ic;
            playerManager.loadItem(ic.identifier, this);
        } else {
            isLoading = false;
        }
    }

    @Override
    public void trackLoaded(AudioTrack at) {
        if (!context.isQuiet()) {
            context.textChannel.sendMessage(
                    gplayer.isPlaying() ? "**" + at.getInfo().title + "** has been added to the queue." : "**" + at.getInfo().title + "** will now play."
            );
        } else {
            log.info("Quietly loaded " + at.getIdentifier());
        }

        trackProvider.add(at);
        if (!gplayer.isPaused()) {
            gplayer.play();
        }
        loadNextAsync();
    }

    @Override
    public void playlistLoaded(AudioPlaylist ap) {
        context.textChannel.sendMessage(
                "Found and added `" + ap.getTracks().size() + "` songs from playlist **" + ap.getName() + "**."
        );

        for (AudioTrack at : ap.getTracks()) {
            trackProvider.add(at);
        }
        if (!gplayer.isPaused()) {
            gplayer.play();
        }
        loadNextAsync();
    }

    @Override
    public void noMatches() {
        context.textChannel.sendMessage("No audio could be found for `" + context.identifier + "`."
        );
        loadNextAsync();
    }

    @Override
    public void loadFailed(FriendlyException fe) {
        if (fe.severity == FriendlyException.Severity.COMMON) {
            context.textChannel.sendMessage("Error when loading info for `" + context.identifier + "`: " + fe.getCause().getMessage()
            );
        } else {
            context.textChannel.sendMessage("Suspicious error when loading info for `" + context.identifier + "`."
            );
            TextUtils.handleException(fe.getCause(), context.textChannel);
        }

        loadNextAsync();
    }

}
