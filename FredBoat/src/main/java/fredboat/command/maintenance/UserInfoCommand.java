package fredboat.command.maintenance;

import fredboat.FredBoat;
import fredboat.commandmeta.abs.Command;
import fredboat.feature.I18n;
import fredboat.util.ArgumentUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by midgard on 17/01/20.
 */
public class UserInfoCommand extends Command{
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        ResourceBundle rb =I18n.get(guild);
        Member target;
        StringBuilder knownServers = new StringBuilder();
        List<Guild> matchguild = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        if(args.length == 1) {
            target = invoker;
        } else {
            target = ArgumentUtil.checkSingleFuzzySearchResult(channel,args[1]);
        }
        if (target == null) return;
        for(Guild g: FredBoat.getAllGuilds()) {
            if(g.getMemberById(target.getUser().getId()) != null) {
                matchguild.add(g);
            }
        }
        if(matchguild.size() >= 30) {
            knownServers.append(matchguild.size());
        } else {
            int i = 0;
            for(Guild g: matchguild) {
                i++;
                knownServers.append(g.getName());
                if(i < matchguild.size()) {
                    knownServers.append(",\n");
                }

            }
        }
        //DMify if I can
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(target.getColor());
        eb.setImage(target.getUser().getAvatarUrl());
        eb.setTitle(MessageFormat.format(rb.getString("userinfoTitle"),target.getUser().getName()));
        eb.addField(rb.getString("userinfoUsername"),target.getUser().getName() + "#" + target.getUser().getDiscriminator(),true);
        eb.addField(rb.getString("userinfoId"),target.getUser().getId(),true);
        eb.addField(rb.getString("userinfoNick"),target.getEffectiveName(),true); //Known Nickname
        eb.addField(rb.getString("userinfoKnownServer"),knownServers.toString(),true); //Known Server
        eb.addField(rb.getString("userinfoJoinDate"),target.getJoinDate().format(dtf),true);
        eb.addField(rb.getString("userinfoCreationTime"),target.getUser().getCreationTime().format(dtf),true);
        //eb.addField(rb.getString("userinfoAvatarUrl"),target.getUser().getAvatarUrl(),true);

        channel.sendMessage(eb.build()).queue();

    }

}
