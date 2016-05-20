package fredboat.command.fun;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class PatCommand extends RandomImageCommand {

    public PatCommand(String[] urls) {
        super(urls);
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        super.onInvoke(guild, channel, invoker, message, args);

        if (message.getMentionedUsers().size() > 0) {
            if (message.getMentionedUsers().get(0) == guild.getJDA().getSelfInfo()) {
                channel.sendMessage("Thanks for the pats :blush:");
            } else {
                channel.sendMessage(new MessageBuilder()
                        .appendString("_Pats ")
                        .appendMention(message.getMentionedUsers().get(0))
                        .appendString("_")
                        .build());
            }
        }
    }

}
