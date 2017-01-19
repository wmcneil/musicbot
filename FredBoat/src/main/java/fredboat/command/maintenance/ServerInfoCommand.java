package fredboat.command.maintenance;

import fredboat.commandmeta.abs.Command;
import fredboat.feature.I18n;
import fredboat.util.BotConstants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by midgard/Chromaryu/knight-ryu12 on 17/01/18.
 */
public class ServerInfoCommand extends Command{
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        ResourceBundle rb = I18n.get(guild);
        int i = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(BotConstants.FREDBOAT_COLOR);
        eb.setTitle(MessageFormat.format(I18n.get(guild).getString("serverinfoTitle"),guild.getName()));
        eb.setThumbnail(guild.getIconUrl());
        for (Member u : guild.getMembers()) {
            if(u.getOnlineStatus() != OnlineStatus.OFFLINE) {
                i++;
            }
        }

        eb.addField(rb.getString("serverinfoOnlineUsers"), String.valueOf(i),true);
        eb.addField(rb.getString("serverinfoTotalUsers"), String.valueOf(guild.getMembers().size()),true);
        eb.addField(rb.getString("serverinfoRoles"), String.valueOf(guild.getRoles().size()),true);
        eb.addField(rb.getString("serverinfoText"), String.valueOf(guild.getTextChannels().size()),true);
        eb.addField(rb.getString("serverinfoVoice"), String.valueOf(guild.getVoiceChannels().size()),true);
        eb.addField(rb.getString("serverinfoCreationDate"), guild.getCreationTime().format(dtf),true);
        eb.addField(rb.getString("serverinfoOwner"), guild.getOwner().getAsMention(),true);
        //eb.setTitle("Hello world");

        channel.sendMessage(eb.build()).queue();
    }
}
