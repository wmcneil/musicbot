/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Frederik Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
        if (isRepeat() && !skipped && lastTrack != null) {
            return lastTrack.makeClone();
        }
        if (isShuffle()) {
            //Get random int from queue, remove it and then return it
            List<Object> list = Arrays.asList(queue.toArray());
            
            if (list.isEmpty()) {
                return null;
            }
            
            lastTrack = (AudioTrack) list.get(new Random().nextInt(list.size()));
            queue.remove(lastTrack);
            return lastTrack;
        } else {
            lastTrack = queue.poll();
            return lastTrack;
        }
    }

    @Override
    public AudioTrack removeAt(int i) {
        if(queue.size() < i){
            return null;
        } else {
            int i2 = 0;
            for(Object obj : Arrays.asList(queue.toArray())){
                if(i == i2){
                    //noinspection SuspiciousMethodCalls
                    queue.remove(obj);
                    return (AudioTrack) obj;
                }
                i2++;
            }
        }

        return null;
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
