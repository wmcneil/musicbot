package fredboat.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.queue.AudioLoader;
import fredboat.audio.queue.IdentifierContext;
import fredboat.audio.queue.SimpleTrackProvider;
import fredboat.commandmeta.MessagingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.utils.PermissionUtil;
import org.slf4j.LoggerFactory;

public class GuildPlayer extends AbstractPlayer {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GuildPlayer.class);

    public static final int MAX_PLAYLIST_ENTRIES = 20;

    public final JDA jda;
    public final String guildId;
    public final HashMap<String, VideoSelection> selections = new HashMap<>();
    public TextChannel currentTC;
    public long lastTimePaused = System.currentTimeMillis();
    public long lastTimeInVC = System.currentTimeMillis();
    public String lastYoutubeVideoId = null;

    private final AudioLoader audioLoader;

    public GuildPlayer(JDA jda, Guild guild) {
        this.jda = jda;
        this.guildId = guild.getId();

        AudioManager manager = guild.getAudioManager();
        manager.setSendingHandler(this);
        audioTrackProvider = new SimpleTrackProvider();
        audioLoader = new AudioLoader(audioTrackProvider, getPlayerManager());
    }

    public void joinChannel(User usr) throws MessagingException {
        VoiceChannel targetChannel = getUserCurrentVoiceChannel(usr);
        joinChannel(targetChannel);
        markIsInVC();
    }

    public void joinChannel(VoiceChannel targetChannel) throws MessagingException {
        if (targetChannel == null) {
            throw new MessagingException("You must join a voice channel first.");
        }

        /*if (guild.getVoiceStatusOfUser(self).inVoiceChannel()) {
            throw new MessagingException("I need to leave my current channel first.");
        }*/
        if (PermissionUtil.checkPermission(targetChannel, jda.getSelfInfo(), Permission.VOICE_CONNECT) == false) {
            throw new MessagingException("I am not permitted to connect to that voice channel.");
        }

        if (PermissionUtil.checkPermission(targetChannel, jda.getSelfInfo(), Permission.VOICE_SPEAK) == false) {
            throw new MessagingException("I am not permitted to play music in that voice channel.");
        }

        AudioManager manager = getGuild().getAudioManager();
        if (manager.getConnectedChannel() != null) {
            manager.moveAudioConnection(targetChannel);
        } else {
            manager.openAudioConnection(targetChannel);
        }

        log.info("Connected to voice channel " + targetChannel);
    }

    public void leaveVoiceChannelRequest(TextChannel channel, boolean silent) {
        AudioManager manager = getGuild().getAudioManager();
        if (!silent) {
            if (manager.getConnectedChannel() == null) {
                channel.sendMessage("Not currently in a channel.");
            } else {
                channel.sendMessage("Left channel " + getChannel().getName() + ".");
            }
        }
        manager.closeAudioConnection();
    }

    public VoiceChannel getUserCurrentVoiceChannel(User usr) {
        for (VoiceChannel chn : getGuild().getVoiceChannels()) {
            for (User userInChannel : chn.getUsers()) {
                if (usr.getId().equals(userInChannel.getId())) {
                    return chn;
                }
            }
        }
        return null;
    }

    public void queue(String identifier, TextChannel channel) {
        queue(identifier, channel, null);
    }

    public void queue(String identifier, TextChannel channel, User invoker) {
        IdentifierContext ic = new IdentifierContext(identifier, channel, invoker);
        audioLoader.loadAsync(ic);
    }

    public int getSongCount() {
        return getRemainingTracks().size();
    }

    public int getTotalRemainingMusicTimeSeconds() {
        int millis = 0;
        for (AudioTrack at : getQueuedTracks()) {
            millis += at.getDuration();
        }

        AudioTrack at = getPlayingTrack();
        if (at != null) {
            millis += at.getDuration() - at.getPosition();
        }

        return millis / 1000;
    }

    public VoiceChannel getChannel() {
        return getUserCurrentVoiceChannel(jda.getSelfInfo());
    }

    public TextChannel getActiveTextChannel() {
        if (currentTC != null) {
            return currentTC;
        } else {
            System.err.println("No currentTC in " + getGuild() + "! Returning public channel...");
            return getGuild().getPublicChannel();
        }

    }

    /**
     * @return Users who are not bots
     */
    public List<User> getUsersInVC() {
        VoiceChannel vc = getChannel();
        if (vc == null) {
            return new ArrayList<>();
        }

        List<User> allUsers = vc.getUsers();
        ArrayList<User> nonBots = new ArrayList<>();
        for (User usr : allUsers) {
            if (!usr.isBot()) {
                nonBots.add(usr);
            }
        }
        return nonBots;
    }

    public long getMillisSincePause() {
        return System.currentTimeMillis() - lastTimePaused;
    }

    public long getMillisSinceInVC() {
        return System.currentTimeMillis() - lastTimeInVC;
    }

    public void markIsInVC() {
        lastTimeInVC = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "[GP:" + getGuild().getId() + "]";
    }

    public Guild getGuild() {
        return jda.getGuildById(guildId);
    }

    public boolean isRepeat() {
        if (audioTrackProvider instanceof SimpleTrackProvider && ((SimpleTrackProvider) audioTrackProvider).isRepeat()) {
            return true;
        }
        return false;
    }
    
    public boolean isShuffle() {
        if (audioTrackProvider instanceof SimpleTrackProvider && ((SimpleTrackProvider) audioTrackProvider).isShuffle()) {
            return true;
        }
        return false;
    }
    
    public void setRepeat(boolean repeat){
        if (audioTrackProvider instanceof SimpleTrackProvider){
            ((SimpleTrackProvider)audioTrackProvider).setRepeat(repeat);
        } else {
            throw new UnsupportedOperationException("Can't repeat or shuffle " + audioTrackProvider.getClass());
        }
    }
    
    public void setShuffle(boolean shuffle){
        if (audioTrackProvider instanceof SimpleTrackProvider){
            ((SimpleTrackProvider)audioTrackProvider).setRepeat(shuffle);
        } else {
            throw new UnsupportedOperationException("Can't repeat or shuffle " + audioTrackProvider.getClass());
        }
    }
    
    public void clear(){
        audioTrackProvider.clear();
    }

}
