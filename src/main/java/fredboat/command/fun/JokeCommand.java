package fredboat.command.fun;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.ICommand;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JokeCommand extends Command implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        try {
            JSONObject object = Unirest.get("http://api.icndb.com/jokes/random").asJson().getBody().getObject();

            if (!"success".equals(object.getString("type"))) {
                throw new RuntimeException("Couldn't gather joke ;|");
            }
            
            String joke = object.getJSONObject("value").getString("joke");
            String remainder = message.getContent().substring(args[0].length()).trim();
            
            if(message.getMentionedUsers().size() > 0){
                joke = joke.replaceAll("Chuck Norris", "<@"+message.getMentionedUsers().get(0).getId()+">");
            } else if (remainder.length() > 0){
                joke = joke.replaceAll("Chuck Norris", remainder);
            }
            
            joke = joke.replaceAll("&quot;", "\"");
            
            channel.sendMessage(joke);
        } catch (UnirestException ex) {
            Logger.getLogger(JokeCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
