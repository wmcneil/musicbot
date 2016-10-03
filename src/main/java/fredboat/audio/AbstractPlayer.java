package fredboat.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.queue.AudioTrackProvider;

public abstract class AbstractPlayer extends AudioEventAdapter {
    
    private static AudioPlayerManager playerManager;
    private AudioPlayer player;
    private AudioTrackProvider audioTrackProvider;

    @SuppressWarnings("LeakingThisInConstructor")
    protected AbstractPlayer() {
        initAudioPlayerManager();
        player = new AudioPlayer(playerManager);
        
        player.addListener(this);
    }
    
    private static void initAudioPlayerManager(){
        playerManager = new AudioPlayerManager();
    }
    
    public void play() {
        if(player.isPaused()){
            player.setPaused(false);
        }
    }
    
    public void pause() {
        player.setPaused(true);
    }
    
    public void skip() {
        player.stopTrack();
    }
    
    public void setVolume(float vol){
        throw new UnsupportedOperationException("AudioPlayer does not yet support volume control");
    }

    public void setAudioTrackProvider(AudioTrackProvider audioTrackProvider) {
        this.audioTrackProvider = audioTrackProvider;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, boolean interrupted) {
        if(audioTrackProvider != null){
            player.playTrack(audioTrackProvider.provideAudioTrack());
        }
    }
    
}
