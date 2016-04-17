package fredboat.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.dv8tion.jda.requests.Requester;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientLogin {

    /*private boolean isTokenValid(String token){
        GetRequest request = Unirest.get("https://discordapp.com/api/users/@me/guilds");
        request.header("Content-Type", "application/json");
        request.header("user-agent", Requester.USER_AGENT);
        request.header("Accept-Encoding", "gzip");
        request.header("Authentication", token);
    }
    
    private String authenticate(){
        
    }
    
    public String clientLogin(String email, String password) {
        String authToken;
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("The provided email or password as empty / null.");
        }
        Path tokenFile = Paths.get("tokens.json");
        JSONObject configs = null;
        String gateway = null;
        if (Files.exists(tokenFile)) {
            configs = readJson(tokenFile);
        }
        if (configs == null) {
            configs = new JSONObject().put("tokens", new JSONObject()).put("version", 1);
        }

        try {
            if (configs.getJSONObject("tokens").has(email)) {
                authToken = configs.getJSONObject("tokens").getString(email);
                try {
                    if (getRequester().getA("https://discordapp.com/api/users/@me/guilds") != null) {
                        System.out.println("Using cached Token: " + authToken);
                    }
                } catch (JSONException ignored) {
                }//token invalid
            }
        } catch (JSONException ex) {
            System.out.println("Token-file misformatted. Please delete it for recreation");
        }

        return null;
    }

    private static JSONObject readJson(Path file) {
        try {
            return new JSONObject(StringUtils.join(Files.readAllLines(file, StandardCharsets.UTF_8), ""));
        } catch (IOException e) {
            System.out.println("Error reading token-file. Defaulting to standard");
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("Token-file misformatted. Creating default one");
        }
        return null;
    }*/

}
