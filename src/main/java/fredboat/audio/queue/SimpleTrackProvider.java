package fredboat.audio.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleTrackProvider extends AbstractTrackProvider {
    
    private ConcurrentLinkedQueue<AudioTrack> queue = new ConcurrentLinkedQueue<>();
    
    protected SimpleTrackProvider(){
    }

    @Override
    public AudioTrack getNext() {
        return queue.peek();
    }
    
    @Override
    public AudioTrack provideAudioTrack() {
        return queue.poll();
    }

    @Override
    public List<AudioTrack> getAsList() {
        return new ArrayList<>(queue);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
