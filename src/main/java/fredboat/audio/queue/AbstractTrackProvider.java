package fredboat.audio.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;

public abstract class AbstractTrackProvider implements AudioTrackProvider {

    private boolean repeat = false;
    private boolean shuffle = false;

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }
    
    public abstract AudioTrack getNext();
    
    public abstract List<AudioTrack> getAsList();
    
    public abstract boolean isEmpty();
    
}
