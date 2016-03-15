package fredboat.command.maintenance;

import fredboat.commandmeta.ICommandOwnerRestricted;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

/**
 *
 * @author frederik
 */
public class TestCommand implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        throw new NullPointerException("Test!");
    }

}
