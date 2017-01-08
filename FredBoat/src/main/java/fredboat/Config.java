/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package fredboat;

import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.util.DiscordUtil;
import fredboat.util.DistributionEnum;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Config {

    private static final Logger log = LoggerFactory.getLogger(Config.class);
    
    public static Config CONFIG;

    public final DistributionEnum distribution;
    public final String botToken;
    public String oauthSecret;
    public final String jdbcUrl;
    public final int numShards;
    public String mashapeKey;
    public String malPassword;
    public int scope;
    public List<String> googleKeys = new ArrayList<>();
    public final String[] lavaplayerNodes = new String[64];;
    public final boolean lavaplayerNodesEnabled;
    public String carbonKey;
    public String cbUser;
    public String cbKey;

    public Config(File credentialsFile, File configFile, int scope) throws IOException, UnirestException {
        this.scope = scope;
        JSONObject creds = new JSONObject(FileUtils.readFileToString(credentialsFile, "UTF-8"));
        JSONObject config = new JSONObject(FileUtils.readFileToString(configFile, "UTF-8"));

        // Determine distribution
        if (config.optBoolean("patron")) {
            distribution = DistributionEnum.PATRON;
        } else if (config.optBoolean("development")) {//Determine distribution
            distribution = DistributionEnum.DEVELOPMENT;
        } else {
            distribution = DiscordUtil.isMainBot() ? DistributionEnum.MAIN : DistributionEnum.MUSIC;
        }

        log.info("Determined distribution: " + distribution);

        mashapeKey = creds.optString("mashapeKey");
        malPassword = creds.optString("malPassword");
        carbonKey = creds.optString("carbonKey");
        cbUser = creds.optString("cbUser");
        cbKey = creds.optString("cbKey");
        botToken = creds.getJSONObject("token").getString(distribution.getId());
        cbKey = creds.optString("cbKey");
        if(creds.has("oauthSecret")){
            oauthSecret = creds.getJSONObject("oauthSecret").optString(distribution.getId());
        }
        if(creds.has("jdbcUrl")){
            jdbcUrl = creds.getString("jdbcUrl");
        } else {
            jdbcUrl = "";
        }

        JSONArray gkeys = creds.optJSONArray("googleServerKeys");
        if (gkeys != null) {
            gkeys.forEach((Object str) -> googleKeys.add((String) str));
        }

        JSONArray nodesArray = creds.optJSONArray("lavaplayerNodes");
        if(nodesArray != null) {
            lavaplayerNodesEnabled = true;
            log.info("Using lavaplayer nodes");
            Iterator<Object> itr = nodesArray.iterator();
            int i = 0;
            while(itr.hasNext()) {
                lavaplayerNodes[i] = (String) itr.next();
                i++;
            }
        } else {
            lavaplayerNodesEnabled = false;
            log.info("Not using lavaplayer nodes. Audio playback will be processed locally.");
        }

        if(distribution == DistributionEnum.DEVELOPMENT) {
            log.info("Development distribution; forcing 2 shards");
            numShards = 2;
        } else {
            numShards = DiscordUtil.getRecommendedShardCount(botToken);
            log.info("Discord recommends " + numShards + " shard(s)");
        }
    }

    public String getRandomGoogleKey() {
        return googleKeys.get((int) Math.floor(Math.random() * googleKeys.size()));
    }

}
