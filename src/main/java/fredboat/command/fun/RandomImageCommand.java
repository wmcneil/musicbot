package fredboat.command.fun;

import fredboat.commandmeta.Command;
import fredboat.util.CacheUtil;
import java.lang.reflect.Array;
import java.util.Random;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class RandomImageCommand extends Command {

    public final String[] urls;

    public RandomImageCommand(String[] urls) {
        this.urls = urls;
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        //Get a random file and send it
        String randomUrl = (String) Array.get(urls, new Random().nextInt(urls.length));
        channel.sendFile(CacheUtil.getImageFromURL(randomUrl), null);
    }

}
