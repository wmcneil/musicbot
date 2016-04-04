package fredboat.command.util;

import fredboat.commandmeta.ICommand;
import fredboat.FredBoat;
import fredboat.commandmeta.Command;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import fredboat.util.TextUtils;

public class HelpCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        invoker.getPrivateChannel().sendMessage(FredBoat.helpMsg);
        channel.sendMessage(TextUtils.prefaceWithMention(invoker, " alright, look at your private messages!"));
    }
    
}
