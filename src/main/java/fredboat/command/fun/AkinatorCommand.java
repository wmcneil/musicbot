package fredboat.command.fun;

import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.FredBoat;
import fredboat.commandmeta.abs.Command;
import fredboat.feature.AkinatorListener;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class AkinatorCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        try {
            AkinatorListener akinator = new AkinatorListener(guild.getJDA(), FredBoat.getListenerBot(), channel.getId(), invoker.getId());
            FredBoat.getListenerBot().putListener(invoker.getId(), akinator);
        } catch (UnirestException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
