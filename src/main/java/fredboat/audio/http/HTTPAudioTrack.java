package fredboat.audio.http;

import com.sedmelluq.discord.lavaplayer.container.matroska.MatroskaAudioTrack;
import com.sedmelluq.discord.lavaplayer.container.mp3.Mp3AudioTrack;
import com.sedmelluq.discord.lavaplayer.container.mpeg.MpegAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.COMMON;
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import java.net.URI;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPAudioTrack extends DelegatedAudioTrack {

    private final Logger log = LoggerFactory.getLogger(HTTPAudioTrack.class);

    private final HTTPAudioSourceManager sourceManager;
    private final String trackUrl;

    public HTTPAudioTrack(AudioTrackInfo trackInfo, HTTPAudioSourceManager sourceManager, String trackUrl) {
        super(trackInfo);
        this.sourceManager = sourceManager;
        this.trackUrl = trackUrl;
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        try (CloseableHttpClient httpClient = sourceManager.createHttpClient()) {
            log.debug("Starting remote HTTP track from URL: {}", trackUrl);

            try (PersistentHttpStream stream = new PersistentHttpStream(httpClient, new URI(trackUrl), null)) {
                if (trackInfo.identifier.endsWith(".mp4") || trackInfo.identifier.endsWith(".m4a")) {
                    processDelegate(new MpegAudioTrack(trackInfo, stream), executor);
                } else if (trackInfo.identifier.endsWith(".webm") || trackInfo.identifier.endsWith(".mkv")) {
                    processDelegate(new MatroskaAudioTrack(trackInfo, stream), executor);
                } else if (trackInfo.identifier.endsWith(".mp3")) {
                    processDelegate(new Mp3AudioTrack(trackInfo, stream), executor);
                } else {
                    throw new FriendlyException("Unknown file extension.", COMMON, null);
                }
            }
        }
    }

    @Override
    public AudioTrack makeClone() {
        return new HTTPAudioTrack(trackInfo, sourceManager, trackUrl);
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return sourceManager;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

}
