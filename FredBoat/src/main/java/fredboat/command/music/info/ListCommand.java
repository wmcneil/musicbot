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

package fredboat.command.music.info;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.audio.queue.AudioTrackContext;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import fredboat.util.TextUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ListCommand extends Command implements IMusicCommand {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ListCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        player.setCurrentTC(channel);
        if (!player.isQueueEmpty()) {
            MessageBuilder mb = new MessageBuilder();

            int numberLength;
            if(player.isShuffle()) {
                numberLength = Integer.toString(player.getSongCount()).length();
                numberLength = Math.max(2, numberLength);
            } else {
                numberLength = 2;
            }

            int i = 0;
            AudioTrackContext firstAtc = null;
            for (AudioTrackContext atc : player.getRemainingTracksOrdered()) {
                log.info(i + ":" + atc.getChronologicalIndex());

                if (i == 0) {
                    firstAtc = atc;
                    String status = player.isPlaying() ? " \\▶" : " \\\u23F8"; //Escaped play and pause emojis
                    mb.append("[" +
                            forceNDigits(player.isShuffle() ? atc.getChronologicalIndex() : atc.getChronologicalIndex() + 1, numberLength)
                            + "]", MessageBuilder.Formatting.BLOCK)
                            .append(status)
                            .append(atc.getTrack().getInfo().title)
                            .append("\n");
                } else {
                    mb.append("[" +
                            forceNDigits(player.isShuffle() && firstAtc.getChronologicalIndex() > atc.getChronologicalIndex() ? atc.getChronologicalIndex() : atc.getChronologicalIndex() + 1, numberLength)
                            + "]", MessageBuilder.Formatting.BLOCK)
                            .append(" " + atc.getTrack().getInfo().title)
                            .append("\n");
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
            
            mb.append("\n" + desc);

            channel.sendMessage(mb.build()).queue();
        } else {
            channel.sendMessage("Not currently playing anything.").queue();
        }
    }

    private String forceNDigits(int i, int n) {
        String str = Integer.toString(i);

        while (str.length() < n) {
            str = "0" + str;
        }

        return str;
    }

    /*private int findFirstShuffledTrackIndex(List<AudioTrackContext> list) {
        int min = list.size();
        boolean first = true;

        for(AudioTrackContext atc : list){
            if(first){
                first = false;
                continue;
            }

            min = Math.min(min, atc.getChronologicalIndex());
        }

        return min -1;
    }*/

}
