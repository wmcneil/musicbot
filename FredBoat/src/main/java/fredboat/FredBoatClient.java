package fredboat;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FredBoatClient extends FredBoat {

    private static final Logger log = LoggerFactory.getLogger(FredBoat.class);

    FredBoatClient () {
        try {
            boolean success = false;
            while (!success) {
                try {
                    jda = new JDABuilder(AccountType.CLIENT)
                            .addListener(listenerSelf)
                            .setToken(clientToken)
                            .buildAsync();

                    success = true;
                } catch (RateLimitedException e) {
                    log.warn("Got rate limited while building client JDA instance! Retrying...", e);
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            log.error("Failed to start JDA client", e);
        }
    }

}
