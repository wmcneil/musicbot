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

package fredboat.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import fredboat.util.TextUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class ListCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild.getId());
        player.setCurrentTC(channel);
        if (!player.isQueueEmpty()) {
            MessageBuilder mb = new MessageBuilder();
            int i = 0;
            for (AudioTrack at : player.getRemainingTracks()) {
                if (i == 0) {
                    String status = player.isPlaying() ? "[PLAYING] " : "[PAUSED] ";
                    mb.appendString(status, MessageBuilder.Formatting.BOLD)
                            .appendString(at.getInfo().title)
                            .appendString("\n");
                } else {
                    mb.appendString(at.getInfo().title)
                            .appendString("\n");
                    if (i == 10) {
                        break;
                    }
                }
                i++;
            }

            //Now add a timestamp for how much is remaining
            long t = player.getTotalRemainingMusicTimeSeconds();
            String timestamp = TextUtils.formatTime(t * 1000L);

            int tracks = player.getRemainingTracks().size() - player.getLiveTracks().size();
            int streams = player.getLiveTracks().size();

            String desc;

            if (tracks == 0) {
                //We are only listening to streams
                desc = "There " + (streams == 1 ? "is" : "are") + " **" + streams +
                        "** live " + (streams == 1 ? "stream" : "streams") + " in the queue.";
            } else {

                desc = "There " + (tracks == 1 ? "is" : "are") + " **" + tracks
                        + "** " + (tracks == 1 ? "track" : "tracks") + " with a remaining length of **[" + timestamp + "]**"
                        + (streams == 0 ? "" : ", as well as **" + streams + "** live " + (streams == 1 ? "stream" : "streams")) + " in the queue.";

            }
            
            mb.appendString("\n" + desc);
            
            channel.sendMessage(mb.build());
        } else {
            channel.sendMessage("Not currently playing anything.");
        }
    }

    public String forceTwoDigits(int i) {
        return i < 10 ? "0" + i : Integer.toString(i);
    }

}
