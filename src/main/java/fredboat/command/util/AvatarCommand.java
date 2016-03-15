package fredboat.command.util;

import fredboat.commandmeta.ICommand;
import fredboat.FredBoat;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import fredboat.util.TextUtils;

public class AvatarCommand implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        if(message.getMentionedUsers().isEmpty()){
            TextUtils.replyWithMention(channel, invoker, " proper usage is: ```;;avatar @<username>```");
        } else {
            TextUtils.replyWithMention(channel, invoker, " found it!\n"+message.getMentionedUsers().get(0).getAvatarUrl());
        }
    }

}
