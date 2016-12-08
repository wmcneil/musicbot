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

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerRegistry {

    private static final HashMap<String, GuildPlayer> REGISTRY = new HashMap<>();
    public static final float DEFAULT_VOLUME = 1f;
    public static JDA jda;

    public static void init(JDA api) {
        jda = api;
    }

    public static void put(String k, GuildPlayer v) {
        REGISTRY.put(k, v);
    }

    public static GuildPlayer get(Guild guild) {
        return get(guild.getId());
    }

    public static GuildPlayer get(String k) {
        GuildPlayer player = REGISTRY.get(k);
        if (player == null) {
            player = new GuildPlayer(jda, jda.getGuildById(k));
            player.setVolume(DEFAULT_VOLUME);
            REGISTRY.put(k, player);
        }
        return player;
    }

    public static GuildPlayer getExisting(Guild guild) {
        return getExisting(guild.getId());
    }

    public static GuildPlayer getExisting(String k) {
        if (REGISTRY.containsKey(k)) {
            return get(k);
        }
        return null;
    }

    public static GuildPlayer remove(String k) {
        return REGISTRY.remove(k);
    }

    public static HashMap<String, GuildPlayer> getRegistry() {
        return REGISTRY;
    }

    public static List<GuildPlayer> getPlayingPlayers() {
        ArrayList<GuildPlayer> plrs = new ArrayList<>();

        for (GuildPlayer plr : REGISTRY.values()) {
            if (plr.isPlaying()) {
                plrs.add(plr);
            }
        }

        return plrs;
    }

    public static void destroyPlayer(Guild g) {
        destroyPlayer(g.getId());
    }

    public static void destroyPlayer(String g) {
        GuildPlayer player = getExisting(g);
        if (player != null) {
            player.destroy();
            remove(g);
        }
    }

}
