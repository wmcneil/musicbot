package fredboat.command.music;

import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.player.source.AudioSource;

public class ListCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild.getId());
        player.currentTC = channel;
        if (player.getCurrentAudioSource() != null || !player.getAudioQueue().isEmpty()) {
            MessageBuilder mb = new MessageBuilder();
            if (player.getCurrentAudioSource() != null) {
                String status = player.isPlaying() ? "[PLAYING] " : "[PAUSED] ";
                mb.appendString(status, MessageBuilder.Formatting.BOLD)
                        .appendString(player.getCurrentAudioSource().getInfo().getTitle())
                        .appendString("\n");
            }

            //Now add the queue
            int i = 1;
            for (AudioSource src : player.getAudioQueue()) {
                mb.appendString(src.getInfo().getTitle())
                        .appendString("\n");
                if (i == 10) {
                    break;
                }
                i++;
            }

            int add = player.getCurrentAudioSource() != null ? 1 : 0;

            //Now add a timestamp for how much is remaining
            int t = player.getTotalRemainingMusicTimeSeconds();
            int sec = t % 60;
            int min = (t % 3600) / 60;
            int hrs = t / 3600;
            
            String timestamp;
            
            if (hrs != 0){
                timestamp = forceTwoDigits(hrs) +":"+ forceTwoDigits(min) +":"+ forceTwoDigits(sec);
            } else {
                timestamp = forceTwoDigits(min) +":"+ forceTwoDigits(sec);
            }

            mb.appendString("\n\nThere are a total of **")
                    .appendString(String.valueOf(player.getAudioQueue().size() + add), MessageBuilder.Formatting.BOLD)
                    .appendString("** queued songs with a remaining length of **" + timestamp + "**.");

            channel.sendMessage(mb.build());
        } else {
            channel.sendMessage("Not currently playing anything");
        }
    }

    public String forceTwoDigits(int i) {
        return i < 10 ? "0" + i : Integer.toString(i);
    }

}
