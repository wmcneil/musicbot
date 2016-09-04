/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fredboat.command.fun;

import fredboat.FredBoat;
import fredboat.commandmeta.Command;
import fredboat.util.BotConstants;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.apache.commons.lang3.StringEscapeUtils;

public class TalkCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        String question = message.getRawContent().substring(BotConstants.DEFAULT_BOT_PREFIX.length() + 5);

        talk(invoker, channel, question);
    }

    public static void talk(User user, TextChannel channel, String question) {
        //Clerverbot integration
        String response = FredBoat.jca.getResponse(question);
        response = user.getUsername() + ": " + StringEscapeUtils.unescapeHtml4(response);
        channel.sendMessage(response);
    }

}
