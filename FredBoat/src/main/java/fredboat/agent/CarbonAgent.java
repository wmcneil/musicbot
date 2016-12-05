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

import fredboat.FredBoat;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.CommandManager;
import fredboat.event.EventListenerBoat;
import fredboat.sharding.FredBoatAPIClient;
import fredboat.sharding.ShardTracker;
import fredboat.util.DiscordUtil;
import net.dv8tion.jda.core.JDA;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarbonAgent extends Thread {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CarbonAgent.class);

    public final String carbonHost;
    public final int CARBON_PORT = 2003;
    public final boolean logProductionStats;
    private int commandsExecutedLastSubmission = 0;
    private int messagesReceivedLastSubmission = 0;
    public final JDA jda;
    public final String buildStream;
    private int minutesWaited = 0;

    public CarbonAgent(JDA jda, String carbonHost, String buildStream, boolean logProductionStats) {
        this.jda = jda;
        this.carbonHost = carbonHost;
        this.buildStream = buildStream;
        this.logProductionStats = logProductionStats;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    waitForNextMinute();

                    minutesWaited++;
                    if (minutesWaited % 5 == 0) {
                        handleEvery5Minutes();
                    }

                    if (minutesWaited % 15 == 0) {
                        handleEvery15Minutes();
                    }

                    if (minutesWaited % 60 == 0) {
                        handleHourly();
                    }

                    //Track command usage
                } catch (InterruptedException ex) {
                    log.error("Carbon agent was interrupted", ex);
                    return;
                } catch (Exception ex) {
                    log.error("Carbon agent caught an exception", ex);
                }
            }

        }
    }

    private void waitForNextMinute() throws InterruptedException {
        long interval = 60000L;
        long currentTerm = System.currentTimeMillis() / interval;
        long nextTermStart = (currentTerm + 1) * interval;
        long diff = nextTermStart - System.currentTimeMillis();

        //Wait for the remaining time
        synchronized (this) {
            this.wait(diff);
        }
    }

    private void handleEvery5Minutes() {
        submitData("carbon.fredboat.memoryUsage." + buildStream + ".shard" + FredBoat.shardId, String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));//In bytes
        if (DiscordUtil.isMusicBot()) {
            submitData("carbon.fredboat.playersPlaying.music." + FredBoat.shardId, String.valueOf(PlayerRegistry.getPlayingPlayers().size()));
        }
    }

    private void handleEvery15Minutes() {
        if (FredBoatAPIClient.isErrornous == false && FredBoat.shardId == 0) {
            submitData("carbon.fredboat.users." + buildStream + ".all", String.valueOf(ShardTracker.getGlobalUserCount()));
            submitData("carbon.fredboat.guilds." + buildStream + ".all", String.valueOf(ShardTracker.getGlobalGuildCount()));
        }
        submitData("carbon.fredboat.users." + buildStream + ".shard" + FredBoat.shardId, String.valueOf(jda.getUsers().size()));
    }

    private void handleHourly() {
        submitData("carbon.fredboat.commandsExecuted." + buildStream + ".shard" + FredBoat.shardId, String.valueOf(CommandManager.commandsExecuted - commandsExecutedLastSubmission));
        commandsExecutedLastSubmission = CommandManager.commandsExecuted;
        submitData("carbon.fredboat.messagesReceived." + buildStream + ".shard" + FredBoat.shardId, String.valueOf(EventListenerBoat.messagesReceived - messagesReceivedLastSubmission));
        messagesReceivedLastSubmission = EventListenerBoat.messagesReceived;
    }

    public void submitData(String path, String value) {
        try {
            String output = path + " " + value + " " + ((System.currentTimeMillis() / 1000) / 60) * 60;
            if (logProductionStats) {
                Socket socket = new Socket(carbonHost, CARBON_PORT);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeBytes(output + "\n");
                dos.flush();
                socket.close();
                log.info("Submitted data: " + output);
            } else {
                log.info("Discarded data: " + output);
            }
        } catch (IOException ex) {
            Logger.getLogger(CarbonAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
