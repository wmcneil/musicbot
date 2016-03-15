package fredboat.command.fun;

import fredboat.commandmeta.ICommand;
import fredboat.util.TextUtils;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class EndTrollCommand implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel c, User invoker, Message message, String[] args) {
        guild.getJDA().getAudioManager().closeAudioConnection();
        TextUtils.replyWithMention(c, invoker, " Closed connection!");
    }
}
