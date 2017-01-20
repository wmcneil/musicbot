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

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BotTest {

    private static final Logger log = LoggerFactory.getLogger(BotTest.class);

    @BeforeAll
    private void setup() {
        File creds = new File("credentials_test.json");
        if(creds.exists()) {
            log.info("Using existing test credentials from credentials_test.json");
        } else {
            log.info("Loading test credentials from TEST_CONF");
            try {
                FileUtils.writeStringToFile(creds, System.getenv("TEST_CONF"), "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException();
            }

            creds.deleteOnExit();
        }

        Config.CONFIG = new Config(creds, new File("config.json"), 0x111);
    }

    @Test
    public void testConfig() {
        Assertions.assertNotNull(Config.CONFIG);
    }

    @Test
    public void testShardBuild() {
        CountDownLatch latch = new CountDownLatch(Config.CONFIG.getNumShards());

        FredBoat.initBotShards(new ListenerAdapter() {
            @Override
            public void onReady(ReadyEvent event) {
                event.getJDA().getPresence().setGame(Game.of("running tests..."));
                latch.countDown();
            }
        });

        try {
            Assertions.assertTrue(latch.await(30, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
