/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Frederik Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fredboat.sharding;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.FredBoat;
import net.dv8tion.jda.entities.User;
import org.json.JSONArray;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class FredBoatAPIClient {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FredBoatAPIClient.class);
    public static boolean isErrornous = true;//This just makes sure we don't submit any stats from failed atempts. 
    
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
                    isErrornous = true;
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
            count = FredBoat.jdaBot.getGuilds().size();
            isErrornous = true;
        }

        return count;
    }

    protected static int getGlobalUserCountFromShards() {
        HashSet<String> map = new HashSet<>();

        for (int i = 0; i < FredBoat.numShards; i++) {
            if (i == FredBoat.shardId) {
                FredBoat.jdaBot.getUsers().forEach((Object user) -> {
                    map.add(((User) user).getId());
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
                    isErrornous = true;
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
            count = FredBoat.jdaBot.getUsers().size();
            isErrornous = true;
        }

        return count;
    }

}
