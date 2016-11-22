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

package fredboat.agent;

import com.mashape.unirest.http.Unirest;
import fredboat.sharding.ShardTracker;
import net.dv8tion.jda.JDA;
import org.slf4j.LoggerFactory;

public class CarbonitexAgent extends Thread {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CarbonitexAgent.class);
    
    private final String key;
    public final JDA jda;

    public CarbonitexAgent(JDA jda, String key) {
        this.jda = jda;
        this.key = key;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (this) {
                    sendStats();
                    sleep(30 * 60 * 1000);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendStats() {
        try {
            final String response = Unirest.post("https://www.carbonitex.net/discord/data/botdata.php").field("key", key)
                    .field("servercount", ShardTracker.getGlobalGuildCount()).asString().getBody();
            log.info("Successfully posted the botdata to carbonitex.com: " + response);
        } catch (Exception e) {
            log.error("An error occured while posting the botdata to carbonitex.com", e);
        }
    }

}
