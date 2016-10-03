package fredboat.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.queue.AudioTrackProvider;
import net.dv8tion.jda.audio.AudioSendHandler;

public abstract class AbstractPlayer extends AudioEventAdapter implements AudioSendHandler {

    private static AudioPlayerManager playerManager;
    private AudioPlayer player;
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
        }
    }

    public void pause() {
        player.setPaused(true);
    }

    public void skip() {
        player.stopTrack();
    }

    public boolean isPlaying() {
        return player.getPlayingTrack() != null && !player.isPaused();
    }

    public long getCurrentTimestamp() {
        return player.getPlayingTrack().getPosition();
    }

    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    public void setVolume(float vol) {
        throw new UnsupportedOperationException("AudioPlayer does not yet support volume control");
    }

    public float getVolume() {
        return -1f;//Not yet supported
    }

    public void setAudioTrackProvider(AudioTrackProvider audioTrackProvider) {
        this.audioTrackProvider = audioTrackProvider;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, boolean interrupted) {
        if (audioTrackProvider != null) {
            player.playTrack(audioTrackProvider.provideAudioTrack());
        }
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

}
