package fredboat.sharding;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.FredBoat;
import java.util.HashSet;
import org.json.JSONArray;
import org.slf4j.LoggerFactory;

public class FredBoatAPIClient {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FredBoatAPIClient.class);

    protected static int getGlobalGuildCountFromShards() {
        int count = 0;

        for (int i = 0; i < FredBoat.numShards; i++) {
            if (i == FredBoat.shardId) {
                count = count + FredBoat.jdaBot.getGuilds().size();
            } else {
                String url = FredBoat.distribution.getUrlForShard(i) + "guildCount";
                try {
                    count = count + Integer.parseInt(Unirest.get(url).headers(FredBoatAPIServer.HEADERS).asString().getBody());
                } catch (UnirestException ex) {
                    log.error("Failed to contact " + url, ex);
                }
            }
        }

        return count;
    }

    protected static int getGlobalGuildCountFromShard0() {
        int count = 0;

        String url = FredBoat.distribution.getUrlForShard(0) + "globalGuildCount";
        try {
            count = count + Integer.parseInt(Unirest.get(url).headers(FredBoatAPIServer.HEADERS).asString().getBody());
        } catch (UnirestException ex) {
            log.error("Failed to contact " + url, ex);
        }

        return count;
    }

    protected static int getGlobalUserCountFromShards() {
        HashSet<String> map = new HashSet<>();

        for (int i = 0; i < FredBoat.numShards; i++) {
            if (i == FredBoat.shardId) {
                FredBoat.jdaBot.getGuilds().forEach((Object id) -> {
                    map.add((String) id);
                });
            } else {
                String url = FredBoat.distribution.getUrlForShard(i) + "users";
                try {
                    JSONArray array = new JSONArray(Unirest.get(url).headers(FredBoatAPIServer.HEADERS).asString().getBody());

                    array.forEach((Object id) -> {
                        map.add((String) id);
                    });
                } catch (UnirestException ex) {
                    log.error("Failed to contact " + url, ex);
                }
            }
        }

        return map.size();
    }

    protected static int getGlobalUserCountFromShard0() {
        int count = 0;

        String url = FredBoat.distribution.getUrlForShard(0) + "globalUserCount";
        try {
            count = count + Integer.parseInt(Unirest.get(url).headers(FredBoatAPIServer.HEADERS).asString().getBody());
        } catch (UnirestException ex) {
            log.error("Failed to contact " + url, ex);
        }

        return count;
    }

}
