package fredboat.sharding;

import fredboat.FredBoat;
import net.dv8tion.jda.JDA;
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
                sleep(TICK_TIME);
                tick();
            } catch (Exception ex) {
                log.error("Caught exception in ShardTracker loop", ex);
            }
        }
    }

    private void tick() {
        if(FredBoat.shardId == 0){
            globalGuildCount = FredBoatAPIClient.getGlobalGuildCountFromShards();
            globalUserCount = FredBoatAPIClient.getGlobalUserCountFromShards();
        } else {
            globalGuildCount = FredBoatAPIClient.getGlobalGuildCountFromShard0();
            globalUserCount = FredBoatAPIClient.getGlobalUserCountFromShard0();
        }
    }

}
