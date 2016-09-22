package fredboat.sharding;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.FredBoat;
import org.slf4j.LoggerFactory;

public class FredBoatAPIClient {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FredBoatAPIClient.class);

    public static int getGlobalGuildCount() {
        int count = 0;

        for (int i = 0; i < FredBoat.numShards; i++) {
            if (i == FredBoat.shardId) {
                count = count + FredBoat.jdaBot.getGuilds().size();
            } else {
                String url = FredBoat.distribution.getUrlForShard(i) + "guildCount";
                try {
                    count = count + Integer.parseInt(Unirest.get(url).asString().getBody());
                } catch (UnirestException ex) {
                    log.error("Failed to contact " + url, ex);
                }
            }
        }

        return count;
    }

}
