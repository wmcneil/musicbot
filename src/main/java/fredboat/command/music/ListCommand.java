package fredboat.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import fredboat.util.TextUtils;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class ListCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild.getId());
        player.setCurrentTC(channel);
        if (!player.isQueueEmpty()) {
            MessageBuilder mb = new MessageBuilder();
            int i = 0;
            for (AudioTrack at : player.getRemainingTracks()) {
                if (i == 0) {
                    String status = player.isPlaying() ? "[PLAYING] " : "[PAUSED] ";
                    mb.appendString(status, MessageBuilder.Formatting.BOLD)
                            .appendString(at.getInfo().title)
                            .appendString("\n");
                } else {
                    mb.appendString(at.getInfo().title)
                            .appendString("\n");
                    if (i == 10) {
                        break;
                    }
                }
                i++;
            }

            //Now add a timestamp for how much is remaining
            long t = player.getTotalRemainingMusicTimeSeconds();
            String timestamp = TextUtils.formatTime(t * 1000L);

            int tracks = player.getRemainingTracks().size() - player.getLiveTracks().size();
            int streams = player.getLiveTracks().size();

            String desc;

            if (tracks == 0) {
                //We are only listening to streams
                desc = "There " + (streams == 1 ? "is" : "are") + " **" + streams +
                        "** live " + (streams == 1 ? "stream" : "streams") + " in the queue.";
            } else {

                desc = "There " + (tracks == 1 ? "is" : "are") + " **" + tracks
                        + "** " + (tracks == 1 ? "track" : "tracks") + " with a remaining length of **[" + timestamp + "]**"
                        + (streams == 0 ? "" : ", as well as **" + streams + "** live " + (streams == 1 ? "stream" : "streams")) + " in the queue.";

            }
            
            mb.appendString("\n" + desc);
            
            channel.sendMessage(mb.build());
        } else {
            channel.sendMessage("Not currently playing anything.");
        }
    }

    public String forceTwoDigits(int i) {
        return i < 10 ? "0" + i : Integer.toString(i);
    }

}
