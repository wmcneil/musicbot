package fredboat.command.music;

import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class UnpauseCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild.getId());
        player.setCurrentTC(channel);
        if (player.isQueueEmpty()) {
            channel.sendMessage("The queue is empty.");
        } else if (!player.isPaused()) {
            channel.sendMessage("The player is not paused.");
        } else if (player.getUsersInVC().isEmpty() && player.isPaused()) {
            channel.sendMessage("There are no users in the voice chat.");
        } else {
            player.play();
            channel.sendMessage("The player is now unpaused.");
        }
    }

}
