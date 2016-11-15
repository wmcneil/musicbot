package fredboat.command.fun;

import fredboat.commandmeta.abs.Command;
import fredboat.util.CacheUtil;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

import java.io.File;

public class RemoteFileCommand extends Command {

    public String msg;
    public File tmpFile;

    public RemoteFileCommand(String msg) {
        this.msg = msg;
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        channel.sendFile(CacheUtil.getImageFromURL(msg), null);
    }

}
