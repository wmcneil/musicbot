package fredboat.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

public class PlayerEventListener extends AudioEventAdapter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PlayerEventListener.class);

    public final GuildPlayer player;
    public Pattern youtubeIdPattern = Pattern.compile("youtube.com\\/watch\\?v=(.+)");

    protected PlayerEventListener(GuildPlayer player) {
        this.player = player;
    }

    @Override
    public void onPlayerPause(AudioPlayer plr) {
        player.lastTimePaused = System.currentTimeMillis();
    }

    @Override
    public void onTrackStart(AudioPlayer plr, AudioTrack track) {
        player.lastYoutubeVideoId = null;//TODO
    }

}
