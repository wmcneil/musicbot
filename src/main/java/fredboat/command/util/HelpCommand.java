package fredboat.command.util;

import fredboat.FredBoat;
import fredboat.commandmeta.Command;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import fredboat.commandmeta.IMusicBackupCommand;

public class HelpCommand extends Command implements IMusicBackupCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        invoker.getPrivateChannel().sendMessage(FredBoat.helpMsg);
        channel.sendMessage(invoker.getUsername() + ": Documentation has been sent to your direct messages!");
    }
    
}
