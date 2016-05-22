package fredboat.command.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.FredBoat;
import fredboat.commandmeta.Command;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class MALCommand extends Command {

    private static Pattern regex = Pattern.compile("^\\S+\\s+([\\W\\w]*)");

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        Matcher matcher = regex.matcher(message.getContent());
        matcher.find();
        String term = matcher.group(1).replace(' ', '+').trim();
        System.out.println("TERM:"+term);

        try {
            String body = Unirest.get("http://myanimelist.net/api/anime/search.xml?q=" + term).basicAuth("FredBoat", FredBoat.MALPassword).asString().getBody();
            if (body != null && body.length() > 0) {
                if(handleAnime(channel, invoker, term, body)){
                    return;
                }
            }

            body = Unirest.get("http://myanimelist.net/search/prefix.json?type=user&keyword=" + term).basicAuth("FredBoat", FredBoat.MALPassword).asString().getBody();
            handleUser(channel, invoker, body);
        } catch (UnirestException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean handleAnime(TextChannel channel, User invoker, String terms, String body) {
        String msg = invoker.getUsername() + ": Search revealed an anime.\n";

        //Read JSON
        JSONObject root = XML.toJSONObject(body);
        JSONObject data = null;
        try {
            data = root.getJSONObject("anime").getJSONArray("entry").getJSONObject(0);
        } catch (JSONException ex) {
            data = root.getJSONObject("anime").getJSONObject("entry");
        }
        
        System.out.println("Anime search deviation: " + data.getString("title").replace(' ', '+').trim().compareToIgnoreCase(terms));
        if(data.getString("title").replace(' ', '+').trim().compareToIgnoreCase(terms) > 3){
            return false;
        }
        
        msg = data.has("title") ? msg + "**Title: **" + data.get("title") + "\n" : msg;
        msg = data.has("english") ? msg + "**English: **" + data.get("english") + "\n" : msg;
        msg = data.has("synonyms") ? msg + "**Synonyms: **" + data.get("synonyms") + "\n" : msg;
        msg = data.has("episodes") ? msg + "**Episodes: **" + data.get("episodes") + "\n" : msg;
        msg = data.has("score") ? msg + "**Score: **" + data.get("score") + "\n" : msg;
        msg = data.has("type") ? msg + "**Type: **" + data.get("type") + "\n" : msg;
        msg = data.has("status") ? msg + "**Status: **" + data.get("status") + "\n" : msg;
        msg = data.has("start_date") ? msg + "**Start date: **" + data.get("start_date") + "\n" : msg;
        msg = data.has("end_date") ? msg + "**End date;: **" + data.get("end_date") + "\n" : msg;
        //msg = data.has("synopsis") ? msg + "**Score: **" + data.get("synopsis") + "\n" : msg;
        msg = data.has("image") ? msg + data.get("image") : msg;

        channel.sendMessage(msg);
        return true;
    }

    private boolean handleUser(TextChannel channel, User invoker, String body) {
        String msg = invoker.getUsername() + ": Search revealed a user.\n";

        //Read JSON
        JSONObject root = new JSONObject(body);
        JSONArray items = root.getJSONArray("categories").getJSONObject(0).getJSONArray("items");
        if(items.length() == 0){
            channel.sendMessage(invoker.getUsername() + ": No results.");
        }
        
        JSONObject data = items.getJSONObject(0);
        
        msg = data.has("name") ? msg + "**Name: **" + data.get("name") + "\n" : msg;
        msg = data.has("url") ? msg + "**URL: **" + data.get("url") + "\n" : msg;
        msg = data.has("image_url") ? msg + data.get("image_url") : msg;

        System.out.println(msg);

        channel.sendMessage(msg);
        return true;
    }

}
