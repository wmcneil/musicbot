package fredboat.command.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.commandmeta.abs.Command;
import fredboat.feature.I18n;
import fredboat.util.DiscordUtil;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;


/**
 * Created by midgard/Chromaryu/knight-ryu12 on 17/01/19.
 */
public class InviteCommand extends Command {
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args)  {
        try {
            String str = "https://discordapp.com/oauth2/authorize?&client_id=" + DiscordUtil.getApplicationInfo(invoker.getJDA().getToken().substring(4)).getString("id") + "&scope=bot";
            String send = MessageFormat.format(I18n.get(guild).getString("invite"),DiscordUtil.getApplicationInfo(message.getJDA().getToken().substring(4)).getString("name"));
            channel.sendMessage(send + "\n" + str).queue();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }


        //return;
    }
}
