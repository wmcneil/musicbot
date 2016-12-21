package fredboat.command.util;

import fredboat.commandmeta.abs.Command;
import fredboat.util.ArgumentUtil;
import fredboat.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class FuzzyUserSearchCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        if(args.length == 1){
            TextUtils.replyWithName(channel, invoker, "Proper usage:\n`;;fuzzy <term>`");
        } else {
            List<Member> list = ArgumentUtil.fuzzyMemberSearch(guild, args[1]);

            if(list.isEmpty()){
                TextUtils.replyWithName(channel, invoker, "No such users");
            }

            String msg = "```\n";
            for(Member member : list){
                msg = msg + member.getEffectiveName() + ",\n";
            }

            msg = msg.substring(0, msg.length() - 2) + "```";

            TextUtils.replyWithName(channel, invoker, msg);
        }
    }
}
