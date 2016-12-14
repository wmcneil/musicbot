package fredboat;

import fredboat.event.EventLogger;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FredBoatBot extends FredBoat {

    private static final Logger log = LoggerFactory.getLogger(FredBoatBot.class);
    private final int shardId;

    FredBoatBot(int shardId) {
        this.shardId = shardId;

        log.info("Building shard " + shardId);

        try {
            boolean success = false;
            while (!success) {
                JDABuilder builder = new JDABuilder(AccountType.BOT)
                        .addListener(listenerBot)
                        .addListener(new EventLogger("216689009110417408"))
                        .setToken(accountToken)
                        .setBulkDeleteSplittingEnabled(true);
                if (numShards > 1) {
                    builder.useSharding(shardId, numShards);
                }
                try {
                    jda = builder.buildAsync();
                    success = true;
                } catch (RateLimitedException e) {
                    log.warn("Got rate limited while building bot JDA instance! Retrying...", e);
                    Thread.sleep(SHARD_CREATION_SLEEP_INTERVAL);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to start JDA shard " + shardId, e);
        }

    }

    public int getShardId() {
        return shardId;
    }

}
