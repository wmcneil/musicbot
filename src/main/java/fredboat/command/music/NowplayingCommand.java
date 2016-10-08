package fredboat.command.music;

import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import fredboat.util.TextUtils;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class NowplayingCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild.getId());
        player.setCurrentTC(channel);
        if (player.isPlaying()) {
            channel.sendMessage("Now playing **" + player.getPlayingTrack().getInfo().title + "** ["
                    + TextUtils.formatTime(player.getPlayingTrack().getDuration())
                    + "]");
        } else {
            channel.sendMessage("Not currently playing anything.");
        }
    }

}
