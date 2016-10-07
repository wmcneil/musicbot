package fredboat.audio.queue;

public abstract class AbstractTrackProvider implements ITrackProvider {

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
    
}
