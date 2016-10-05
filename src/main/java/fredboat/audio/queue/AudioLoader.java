package fredboat.audio.queue;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.GuildPlayer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AudioLoader implements AudioLoadResultHandler {

    private final ITrackProvider trackProvider;
    private final AudioPlayerManager playerManager;
    private final GuildPlayer gplayer;
    private final ConcurrentLinkedQueue<IdentifierContext> identifierQueue = new ConcurrentLinkedQueue();
    private IdentifierContext contextBeingLoaded = null;
    private volatile boolean isLoading = false;

    public AudioLoader(ITrackProvider trackProvider, AudioPlayerManager playerManager, GuildPlayer gplayer) {
        this.trackProvider = trackProvider;
        this.playerManager = playerManager;
        this.gplayer = gplayer;
    }
    
    public void loadAsync(IdentifierContext ic){
        identifierQueue.add(ic);
        if(!isLoading){
            loadNextAsync();
        }
    }
    
    private void loadNextAsync(){
        IdentifierContext ic = identifierQueue.poll();
        if(ic != null){
            isLoading = true;
            contextBeingLoaded = ic;
            playerManager.loadItem(ic.identifier, this);
        } else {
            isLoading = false;
        }
    }
    
    @Override
    public void trackLoaded(AudioTrack at) {
        contextBeingLoaded.textChannel.sendMessage(
                "**" + at.getInfo().title + "** has been added to the queue."
        );
        
        trackProvider.add(at);
        if(!gplayer.isPaused()){
            gplayer.play();
        }
        loadNextAsync();
    }

    @Override
    public void playlistLoaded(AudioPlaylist ap) {
        contextBeingLoaded.textChannel.sendMessage(
                "Found and added `" + ap.getTracks().size() + "` songs from playlist **" + ap.getName() + "**."
        );
        
        for(AudioTrack at : ap.getTracks()){
            trackProvider.add(at);
        }
        if(!gplayer.isPaused()){
            gplayer.play();
        }
        loadNextAsync();
    }

    @Override
    public void noMatches() {
        contextBeingLoaded.textChannel.sendMessage(
                "No audio could be found for `" + contextBeingLoaded.identifier + "`."
        );
        loadNextAsync();
    }

    @Override
    public void loadFailed(FriendlyException fe) {
        contextBeingLoaded.textChannel.sendMessage(
                "Error when loading info for `" + contextBeingLoaded.identifier + "`: " + fe.getMessage()
        );
        loadNextAsync();
    }
    
}
