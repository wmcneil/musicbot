package fredboat.command.maintenance;

import fredboat.FredBoat;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.ICommandOwnerRestricted;
import fredboat.util.BotConstants;
import fredboat.util.ExitCodes;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import fredboat.util.TextUtils;

/**
 *
 * @author frederik
 */
public class ExitCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        if (invoker.getId().equals(BotConstants.OWNER_ID)) {
            channel.sendMessage(TextUtils.prefaceWithMention(invoker, " goodbye!!"));
            FredBoat.shutdown(ExitCodes.EXIT_CODE_NORMAL);
        } else {
            channel.sendMessage(TextUtils.prefaceWithMention(invoker, " you are not allowed to use that command!"));
        }
    }

}
