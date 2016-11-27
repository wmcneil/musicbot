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

package fredboat.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.FredBoat;
import fredboat.audio.queue.IdentifierContext;
import fredboat.util.ExitCodes;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class MusicPersistenceHandler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MusicPersistenceHandler.class);

    private MusicPersistenceHandler() {
    }

    public static void handlePreShutdown(int code) {
        JDA jda = FredBoat.jdaBot;

        File dir = new File("music_persistence");
        if (!dir.exists()) {
            dir.mkdir();
        }
        HashMap<String, GuildPlayer> reg = PlayerRegistry.getRegistry();

        boolean isUpdate = code == ExitCodes.EXIT_CODE_UPDATE;
        boolean isRestart = code == ExitCodes.EXIT_CODE_RESTART;

        String msg = null;

        if (isUpdate) {
            msg = "FredBoat♪♪ is updating. This should only take a minute and will reload the current playlist.";
        } else if (isRestart) {
            msg = "FredBoat♪♪ is restarting. This should only take a minute and will reload the current playlist.";
        } else {
            msg = "FredBoat♪♪ is shutting down. Once the bot comes back the current playlist will reload.";
        }

        for (String gId : reg.keySet()) {
            try {
                GuildPlayer player = reg.get(gId);

                if (!player.isPlaying()) {
                    continue;//Nothing to see here
                }

                player.getActiveTextChannel().sendMessage(msg);

                JSONObject data = new JSONObject();
                data.put("vc", player.getUserCurrentVoiceChannel(jda.getSelfInfo()).getId());
                data.put("tc", player.getActiveTextChannel().getId());
                data.put("isPaused", player.isPaused());
                data.put("volume", Float.toString(player.getVolume()));
                data.put("repeat", player.isRepeat());
                data.put("shuffle", player.isShuffle());

                ArrayList<String> identifiers = new ArrayList<>();
                
                for (AudioTrack at : player.getRemainingTracks()) {
                    identifiers.add(at.getIdentifier());
                }

                if (player.getPlayingTrack() != null) {
                    data.put("position", player.getPlayingTrack().getPosition());
                }

                data.put("sources", identifiers);

                try {
                    FileUtils.writeStringToFile(new File(dir, gId), data.toString(), Charset.forName("UTF-8"));
                } catch (IOException ex) {
                    player.getActiveTextChannel().sendMessage("Error occured when saving persistence file: " + ex.getMessage());
                }
            } catch (Exception ex) {
                log.error("Error when saving persistence file", ex);
            }
        }
    }

    public static void reloadPlaylists() {
        JDA jda = FredBoat.jdaBot;

        File dir = new File("music_persistence");
        if (!dir.exists()) {
            return;
        }
        log.info("Found persistence data: " + Arrays.toString(dir.listFiles()));

        for (File file : dir.listFiles()) {
            InputStream is = null;
            try {
                String gId = file.getName();
                is = new FileInputStream(file);
                Scanner scanner = new Scanner(is);
                JSONObject data = new JSONObject(scanner.useDelimiter("\\A").next());
                scanner.close();

                GuildPlayer player = PlayerRegistry.get(gId);

                boolean isPaused = data.getBoolean("isPaused");
                final JSONArray sources = data.getJSONArray("sources");
                VoiceChannel vc = jda.getVoiceChannelById(data.getString("vc"));
                TextChannel tc = jda.getTextChannelById(data.getString("tc"));
                float volume = Float.parseFloat(data.getString("volume"));
                boolean repeat = data.getBoolean("repeat");
                boolean shuffle = data.getBoolean("shuffle");

                player.joinChannel(vc);
                player.setCurrentTC(tc);
                player.setVolume(volume);
                player.setRepeat(repeat);
                player.setShuffle(shuffle);

                sources.forEach((Object t) -> {
                    String identifier = (String) t;

                    IdentifierContext ic = new IdentifierContext(identifier, tc);

                    ic.setQuiet(true);

                    if (identifier.equals(sources.get(0))) {
                        if (data.has("position")) {
                            ic.setPosition(data.getLong("position"));
                        }
                    }

                    player.queue(ic);
                });

                player.setPause(isPaused);
                tc.sendMessage("Started reloading playlist :ok_hand::skin-tone-3:");
            } catch (Exception ex) {
                log.error("Error when loading persistence file", ex);
            } finally {
                try {
                    is.close();
                } catch (Exception ex) {
                    log.error("Error when closing InputStream after error", ex);
                }
            }
        }

        for (File f : dir.listFiles()) {
            boolean deleted = f.delete();
            log.info(deleted ? "Deleted persistence file: " + f : "Failed to delete persistence file: " + f);
        }

        dir.delete();
    }

}
