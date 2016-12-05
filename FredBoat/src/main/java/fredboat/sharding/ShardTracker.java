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

import fredboat.FredBoat;
import net.dv8tion.jda.core.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShardTracker extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ShardTracker.class);
    public final JDA jda;
    public static ShardTracker ins = null;

    private static int globalUserCount = 0;
    private static int globalGuildCount = 0;

    private static final long TICK_TIME = 10 * 60 * 1000;//10 mins

    public ShardTracker(JDA jda, String token) {
        if (ins != null) {
            throw new IllegalStateException("Only one instance may exist.");
        }

        this.jda = jda;

        setDaemon(true);
        setName(this.getClass().getSimpleName());
        ins = this;
    }

    public static int getGlobalUserCount() {
        return globalUserCount;
    }

    public static int getGlobalGuildCount() {
        return globalGuildCount;
    }

    @Override
    public void run() {
        while (true) {
            try {
                tick();
                sleep(TICK_TIME);
            } catch (Exception ex) {
                log.error("Caught exception in ShardTracker loop", ex);
            }
        }
    }

    private void tick() {
        FredBoatAPIClient.isErrornous = false;
        
        if(FredBoat.shardId == 0){
            globalGuildCount = FredBoatAPIClient.getGlobalGuildCountFromShards();
            globalUserCount = FredBoatAPIClient.getGlobalUserCountFromShards();
        } else {
            globalGuildCount = FredBoatAPIClient.getGlobalGuildCountFromShard0();
            globalUserCount = FredBoatAPIClient.getGlobalUserCountFromShard0();
        }
    }

}
