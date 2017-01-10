/*
 * MIT License
 *
 * Copyright (c) 2016 Frederik Ar. Mikkelsen
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

package fredboat.command.maintenance;

import fredboat.FredBoat;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.CommandManager;
import fredboat.commandmeta.abs.Command;
import fredboat.feature.I13n;
import fredboat.util.DiscordUtil;
import fredboat.util.TextUtils;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

public class StatsCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        long totalSecs = (System.currentTimeMillis() - FredBoat.START_TIME) / 1000;
        int days = (int) (totalSecs / (60 * 60 * 24));
        int hours = (int) ((totalSecs / (60 * 60)) % 24);
        int mins = (int) ((totalSecs / 60) % 60);
        int secs = (int) (totalSecs % 60);
        
        String str = MessageFormat.format(
                I13n.get(guild).getString("statsParagraph"),
                days, hours, mins, secs, CommandManager.commandsExecuted - 1);
        
        str = MessageFormat.format(I13n.get(guild).getString("statsRate"), str, (float) (CommandManager.commandsExecuted - 1) / ((float) totalSecs / (float) (60 * 60)));
        str = MessageFormat.format(I13n.get(guild).getString("statsResMem"), str, Runtime.getRuntime().totalMemory() / 1000000);
        str = MessageFormat.format(I13n.get(guild).getString("statsMemUsed"), str, (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000);
        str = MessageFormat.format(I13n.get(guild).getString("statsMemFree"), str, Runtime.getRuntime().freeMemory() / 1000000);
        str = MessageFormat.format(I13n.get(guild).getString("statsMaxRes"), str, Runtime.getRuntime().maxMemory() / 1000000);

        str = str + "\n----------\n\n";

        str = MessageFormat.format(I13n.get(guild).getString("statsSharding"), str, FredBoat.getInstance(guild.getJDA()).getShardInfo().getShardString());
        if(DiscordUtil.isMusicBot()){
            str = MessageFormat.format(I13n.get(guild).getString("statsPlaying"), str, PlayerRegistry.getPlayingPlayers().size());
        }
        str = MessageFormat.format(I13n.get(guild).getString("statsServers"), str, FredBoat.getAllGuilds().size());
        str = MessageFormat.format(I13n.get(guild).getString("statsUsers"), str, FredBoat.getAllUsersAsMap().size());
        str = MessageFormat.format(I13n.get(guild).getString("statsDistribution"), str, FredBoat.distribution);
        str = MessageFormat.format(I13n.get(guild).getString("statsJDAResp"), str, guild.getJDA().getResponseTotal());
        str = MessageFormat.format(I13n.get(guild).getString("statsJDAVersion"), str, JDAInfo.VERSION);
        
        str = str + "```";

        channel.sendMessage(TextUtils.prefaceWithName(invoker, str)).queue();
    }

}
