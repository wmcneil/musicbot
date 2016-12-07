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

package fredboat.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.queue.AudioLoader;
import fredboat.audio.queue.IdentifierContext;
import fredboat.audio.queue.SimpleTrackProvider;
import fredboat.commandmeta.MessagingException;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildPlayer extends AbstractPlayer {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GuildPlayer.class);

    public static final int MAX_PLAYLIST_ENTRIES = 20;

    public final JDA jda;
    public final String guildId;
    public final Map<String, VideoSelection> selections = new HashMap<>();
    private TextChannel currentTC;
    public String lastYoutubeVideoId = null;

    private final AudioLoader audioLoader;

    @SuppressWarnings("LeakingThisInConstructor")
    public GuildPlayer(JDA jda, Guild guild) {
        this.jda = jda;
        this.guildId = guild.getId();

        guild.

        AudioManager manager = guild.getAudioManager();
        manager.setSendingHandler(this);
        audioTrackProvider = new SimpleTrackProvider();
        audioLoader = new AudioLoader(audioTrackProvider, getPlayerManager(), this);
    }

    public void joinChannel(Member usr) throws MessagingException {
        VoiceChannel targetChannel = getUserCurrentVoiceChannel(usr);
        joinChannel(targetChannel);
    }

    public void joinChannel(VoiceChannel targetChannel) throws MessagingException {
        if (targetChannel == null) {
            throw new MessagingException("You must join a voice channel first.");
        }

        if (!PermissionUtil.checkPermission(targetChannel, jda.getSelfInfo(), Permission.VOICE_CONNECT)) {
            throw new MessagingException("I am not permitted to connect to that voice channel.");
        }

        if (!PermissionUtil.checkPermission(targetChannel, jda.getSelfInfo(), Permission.VOICE_SPEAK)) {
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

    public VoiceChannel getUserCurrentVoiceChannel(Member member) {
        for (VoiceChannel chn : getGuild().getVoiceChannels()) {
            for (Member memberInChannel : chn.getMembers()) {
                if (member.getUser().getId().equals(memberInChannel.getUser().getId())) {
                    return chn;
                }
            }
        }
        return null;
    }

    public void queue(String identifier, TextChannel channel) {
        queue(identifier, channel, null);
    }

    public void queue(String identifier, TextChannel channel, Member invoker) {
        IdentifierContext ic = new IdentifierContext(identifier, channel, invoker);

        if (invoker != null) {
            joinChannel(invoker);
        }

        audioLoader.loadAsync(ic);
    }

    public void queue(IdentifierContext ic) {
        if (ic.user != null) {
            joinChannel(ic.user);
        }

        audioLoader.loadAsync(ic);
    }

    public int getSongCount() {
        return getRemainingTracks().size();
    }

    public long getTotalRemainingMusicTimeSeconds() {
        //Live streams are considered to have a length of 0
        long millis = 0;
        for (AudioTrack at : getQueuedTracks()) {
            if (!at.getInfo().isStream) {
                millis += at.getDuration();
            }
        }

        AudioTrack at = getPlayingTrack();
        if (at != null && !at.getInfo().isStream) {
            millis += Math.max(0, at.getDuration() - at.getPosition());
        }

        return millis / 1000;
    }
    
    public List<AudioTrack> getLiveTracks() {
        ArrayList<AudioTrack> l = new ArrayList<>();
        
        for(AudioTrack at : getRemainingTracks()){
            if(at.getInfo().isStream){
                l.add(at);
            }
        }
        
        return l;
    }

    public VoiceChannel getChannel() {
        return getUserCurrentVoiceChannel(jda.getSelfInfo());
    }

    public TextChannel getActiveTextChannel() {
        if (currentTC != null) {
            return currentTC;
        } else {
            log.warn("No currentTC in " + getGuild() + "! Returning public channel...");
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

    public void setRepeat(boolean repeat) {
        if (audioTrackProvider instanceof SimpleTrackProvider) {
            ((SimpleTrackProvider) audioTrackProvider).setRepeat(repeat);
        } else {
            throw new UnsupportedOperationException("Can't repeat or shuffle " + audioTrackProvider.getClass());
        }
    }

    public void setShuffle(boolean shuffle) {
        if (audioTrackProvider instanceof SimpleTrackProvider) {
            ((SimpleTrackProvider) audioTrackProvider).setShuffle(shuffle);
        } else {
            throw new UnsupportedOperationException("Can't repeat or shuffle " + audioTrackProvider.getClass());
        }
    }

    public void setCurrentTC(TextChannel currentTC) {
        this.currentTC = currentTC;
    }

    public TextChannel getCurrentTC() {
        return currentTC;
    }

    public void clear() {
        audioTrackProvider.clear();
    }

}
