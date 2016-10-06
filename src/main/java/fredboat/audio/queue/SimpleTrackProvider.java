package fredboat.audio.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleTrackProvider extends AbstractTrackProvider {
    
    private volatile ConcurrentLinkedQueue<AudioTrack> queue = new ConcurrentLinkedQueue<>();
    private AudioTrack lastTrack = null;
    
    @Override
    public AudioTrack getNext() {
        return queue.peek();
    }
    
    @Override
    public AudioTrack provideAudioTrack(boolean skipped) {
        if(isRepeat() && lastTrack != null){
            return lastTrack.makeClone();
        }
        if(isShuffle()){
            //Get random int from queue, remove it and then return it
            List<Object> list = Arrays.asList(queue.toArray());
            lastTrack = (AudioTrack) list.get(new Random().nextInt(list.size()));
            queue.remove(lastTrack);
            return lastTrack;
        } else {
            lastTrack = queue.poll();
            return lastTrack;
        }
    }

    @Override
    public List<AudioTrack> getAsList() {
        return new ArrayList<>(queue);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void add(AudioTrack track) {
        queue.add(track);
    }

    @Override
    public void clear() {
        queue.clear();
    }
}
