package fredboat.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.queue.AudioTrackProvider;
import net.dv8tion.jda.audio.AudioSendHandler;

public abstract class AbstractPlayer extends AudioEventAdapter implements AudioSendHandler {

    private static AudioPlayerManager playerManager;
    AudioPlayer player;
    private AudioTrackProvider audioTrackProvider;

    @SuppressWarnings("LeakingThisInConstructor")
    protected AbstractPlayer() {
        initAudioPlayerManager();
        player = new AudioPlayer(playerManager);

        player.addListener(this);
    }

    private static void initAudioPlayerManager() {
        playerManager = new AudioPlayerManager();
    }

    public void play() {
        if (player.isPaused()) {
            player.setPaused(false);
            if (player.getPlayingTrack() == null) {
                play0();
            }
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
    
    public void pause(){
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

    public void setVolume(float vol) {
        player.setVolume((int) (vol * 100));
    }

    public float getVolume() {
        return ((float) player.getVolume()) * 100;
    }

    public void setAudioTrackProvider(AudioTrackProvider audioTrackProvider) {
        this.audioTrackProvider = audioTrackProvider;
    }

    public AudioTrackProvider getAudioTrackProvider() {
        return audioTrackProvider;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, boolean interrupted) {
        //If we *were* interrupted, we would just invoke play0()
        if (!interrupted) {
            play0();
        }
    }

    private void play0() {
        if (audioTrackProvider != null) {
            player.playTrack(audioTrackProvider.provideAudioTrack());
        }
    }

    public void destroy() {
        player.destroy();
    }

    @Override
    public boolean canProvide() {
        return isPlaying();
    }

    @Override
    public byte[] provide20MsAudio() {
        return player.provide().data;
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
