package fredboat.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fredboat.audio.queue.AudioTrackContext;
import fredboat.db.DatabaseNotReadyException;
import fredboat.db.EntityReader;
import fredboat.db.entities.GuildConfig;
import fredboat.feature.I18n;

import java.text.MessageFormat;

class PlayerEventListener extends AudioEventAdapter {

    private final GuildPlayer guildPlayer;

    PlayerEventListener(GuildPlayer guildPlayer) {
        this.guildPlayer = guildPlayer;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioTrackContext next = guildPlayer.audioTrackProvider.getNext();
        boolean enabled = false;
        try {
            GuildConfig config = EntityReader.getGuildConfig(guildPlayer.guildId);
            enabled = config.isTrackAnnounce();
        } catch (DatabaseNotReadyException ignored) {}
        if(enabled
                && (endReason == AudioTrackEndReason.FINISHED || endReason == AudioTrackEndReason.STOPPED)
                && next != null
                && !guildPlayer.isRepeat()){
            guildPlayer.getActiveTextChannel().sendMessage(MessageFormat.format(I18n.get(guildPlayer.getGuild()).getString("trackAnnounce"), next.getEffectiveTitle())).queue();
        }
    }
}
