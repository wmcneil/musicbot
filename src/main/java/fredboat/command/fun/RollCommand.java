package fredboat.command.fun;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class RollCommand extends RandomImageCommand {

    public RollCommand(String[] urls) {
        super(urls);
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        super.onInvoke(guild, channel, invoker, message, args);

        if (message.getMentionedUsers().size() > 0) {
            
                channel.sendMessage(new MessageBuilder()
                        .appendString("_")
                        .appendMention(invoker)
                        .appendString(" rolls around on the floor._")
                        .build());
            
        }
    }

}
