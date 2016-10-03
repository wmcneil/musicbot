package fredboat.audio.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface AudioTrackProvider {
    
    public AudioTrack provideAudioTrack();
    
}
