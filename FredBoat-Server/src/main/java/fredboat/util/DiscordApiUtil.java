package fredboat.util;

import fredboat.server.webentity.WebUser;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.common.CommonConstants;
import org.json.JSONObject;

public class DiscordApiUtil {

    public static WebUser getCurrentUser(String bearer) {
        try {
            JSONObject json = Unirest.get(CommonConstants.DISCORD_API_BASE + "/users/{@me}")
                    .header("user-agent", CommonConstants.USER_AGENT)
                    .header("Authorization", "Bearer " + bearer)
                    .asJson().getBody().getObject();

            WebUser user = new WebUser(
                    json.getString("id"),
                    json.getString("username"),
                    json.getString("discriminator"),
                    json.getString("avatar"),
                    json.getBoolean("bot")
            );
            
            return user;
        } catch (UnirestException ex) {
            throw new RuntimeException();
        }
    }

}
