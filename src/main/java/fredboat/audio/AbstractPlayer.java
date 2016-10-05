package fredboat.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.audio.AudioSendHandler;
import fredboat.audio.queue.ITrackProvider;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlayer extends AudioEventAdapter implements AudioSendHandler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AbstractPlayer.class);
    
    private static AudioPlayerManager playerManager;
    AudioPlayer player;
    ITrackProvider audioTrackProvider;
    private AudioFrame lastFrame = null;

    @SuppressWarnings("LeakingThisInConstructor")
    protected AbstractPlayer() {
        initAudioPlayerManager();
        player = new AudioPlayer(playerManager);

        player.addListener(this);
    }

    private static void initAudioPlayerManager() {
        playerManager = new AudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
    }

    public void play() {
        if (player.isPaused()) {
            player.setPaused(false);
        }
        if (player.getPlayingTrack() == null) {
            play0();
        }

    }

    public void setPause(boolean pause) {
        if (pause) {
            player.setPaused(true);
        } else {
            player.setPaused(false);
            play();
        }
    }

    public void pause() {
        player.setPaused(true);
    }

    public void stop() {
        player.stopTrack();
    }

    public void skip() {
        player.stopTrack();
        play0();
    }

    public long getCurrentTimestamp() {
        return player.getPlayingTrack().getPosition();
    }

    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    public List<AudioTrack> getQueuedTracks() {
        return audioTrackProvider.getAsList();
    }

    public List<AudioTrack> getRemainingTracks() {
        //Includes currently playing track, which comes first
        if (getPlayingTrack() != null) {
            ArrayList<AudioTrack> list = new ArrayList<>();
            list.add(getPlayingTrack());
            list.addAll(getQueuedTracks());
            return list;
        } else {
            return getQueuedTracks();
        }
    }

    public void setVolume(float vol) {
        player.setVolume((int) (vol * 100));
    }

    public float getVolume() {
        return ((float) player.getVolume()) * 100;
    }

    public void setAudioTrackProvider(ITrackProvider audioTrackProvider) {
        this.audioTrackProvider = audioTrackProvider;
    }

    public ITrackProvider getAudioTrackProvider() {
        return audioTrackProvider;
    }

    public static AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, boolean interrupted) {
        //If we *were* interrupted, we would just invoke play0()
        log.debug("Track ended. Interrupted: " + interrupted + ", player: " + player);
        if (!interrupted) {
            play0();
        }
    }

    private void play0() {
        if (audioTrackProvider != null) {
            player.playTrack(audioTrackProvider.provideAudioTrack());
        } else {
            log.warn("TrackProvider doesn't exist");
        }
    }

    public void destroy() {
        player.destroy();
    }

    @Override
    public byte[] provide20MsAudio() {
        return lastFrame.data;
    }
    
    @Override
    public boolean canProvide() {
        lastFrame = player.provide();
        
        return lastFrame != null;
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    public boolean isPlaying() {
        return player.getPlayingTrack() != null && !player.isPaused();
    }

    public boolean isPaused() {
        return player.isPaused();
    }

}
