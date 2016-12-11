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

import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import fredboat.util.TextUtils;
import fredboat.util.YoutubeAPI;
import fredboat.util.YoutubeVideo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import javax.xml.soap.Text;
import java.awt.Color;

public class NowplayingCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild.getId());
        player.setCurrentTC(channel);
        if (player.isPlaying()) {

            AudioTrack at = player.getPlayingTrack();

            if (at instanceof YoutubeAudioTrack) {
                sendYoutubeEmbed(channel, (YoutubeAudioTrack) at);
            } else if (at instanceof SoundCloudAudioTrack) {
                sendSoundcloudEmbed(channel, (SoundCloudAudioTrack) at);
            } else if (at instanceof HttpAudioTrack) {
                sendHttpEmbed(channel, (HttpAudioTrack) at);
            } else if (at instanceof BandcampAudioTrack) {
                sendBandcampResponse(channel, (BandcampAudioTrack) at);
            } else if (at instanceof TwitchStreamAudioTrack) {
                sendTwitchEmbed(channel, (TwitchStreamAudioTrack) at);
            } else {
                sendDefaultEmbed(channel, at);
            }

        } else {
            channel.sendMessage("Not currently playing anything.").queue();
        }
    }

    private void sendYoutubeEmbed(TextChannel channel, YoutubeAudioTrack at){
        YoutubeVideo yv = YoutubeAPI.getVideoFromID(at.getIdentifier(), true);
        String desc = "["
                + TextUtils.formatTime(at.getPosition())
                + "/"
                + TextUtils.formatTime(at.getDuration())
                + "]\n\n" + yv.getDescription();

        //Shorten it to about 400 chars if it's too long
        if(desc.length() > 450){
            desc = TextUtils.substringPreserveWords(desc, 400, " [...]");
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(at.getInfo().title)
                .setUrl("https://www.youtube.com/watch?v=" + at.getIdentifier())
                .setDescription(desc)
                .setColor(new Color(205, 32, 31))
                .setThumbnail("https://i.ytimg.com/vi/" + at.getIdentifier() + "/hqdefault.jpg")
                .setAuthor(yv.getCannelTitle(), yv.getChannelUrl(), yv.getChannelThumbUrl())
                .setFooter(channel.getJDA().getSelfUser().getName(), channel.getJDA().getSelfUser().getAvatarUrl())
                .build();
        channel.sendMessage(embed).queue();
    }

    private void sendSoundcloudEmbed(TextChannel channel, SoundCloudAudioTrack at) {
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(at.getInfo().author, null, null)
                .setTitle(at.getInfo().title)
                .setDescription("["
                        + TextUtils.formatTime(at.getPosition())
                        + "/"
                        + TextUtils.formatTime(at.getDuration())
                        + "]\n\nLoaded from Soundcloud") //TODO: Gather description, thumbnail, etc
                .setColor(new Color(255, 85, 0))
                .setFooter(channel.getJDA().getSelfUser().getName(), channel.getJDA().getSelfUser().getAvatarUrl())
                .build();

        channel.sendMessage(embed).queue();
    }

    private void sendBandcampResponse(TextChannel channel, BandcampAudioTrack at){
        String desc = at.getDuration() == Long.MAX_VALUE ?
                "[LIVE]" :
                "["
                        + TextUtils.formatTime(at.getPosition())
                        + "/"
                        + TextUtils.formatTime(at.getDuration())
                        + "]";

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(at.getInfo().author, null, null)
                .setTitle(at.getInfo().title)
                .setDescription(desc + "\n\nLoaded from Bandcamp")
                .setColor(new Color(99, 154, 169))
                .setFooter(channel.getJDA().getSelfUser().getName(), channel.getJDA().getSelfUser().getAvatarUrl())
                .build();

        channel.sendMessage(embed).queue();
    }

    private void sendTwitchEmbed(TextChannel channel, TwitchStreamAudioTrack at){
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(at.getInfo().author, at.getIdentifier(), null) //TODO: Add thumb
                .setTitle(at.getInfo().title)
                .setDescription("Loaded from Twitch")
                .setColor(new Color(100, 65, 164))
                .setFooter(channel.getJDA().getSelfUser().getName(), channel.getJDA().getSelfUser().getAvatarUrl())
                .build();

        channel.sendMessage(embed).queue();
    }

    private void sendHttpEmbed(TextChannel channel, HttpAudioTrack at){
        String desc = at.getDuration() == Long.MAX_VALUE ?
                "[LIVE]" :
                "["
                        + TextUtils.formatTime(at.getPosition())
                        + "/"
                        + TextUtils.formatTime(at.getDuration())
                        + "]";

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(at.getInfo().author, null, null)
                .setTitle(at.getInfo().title)
                .setUrl(at.getIdentifier())
                .setDescription(desc + "\n\nLoaded from " + at.getIdentifier()) //TODO: Probe data
                .setColor(new Color(28, 191, 226))
                .setFooter(channel.getJDA().getSelfUser().getName(), channel.getJDA().getSelfUser().getAvatarUrl())
                .build();

        channel.sendMessage(embed).queue();
    }

    private void sendDefaultEmbed(TextChannel channel, AudioTrack at){
        String desc = at.getDuration() == Long.MAX_VALUE ?
                "[LIVE]" :
                "["
                        + TextUtils.formatTime(at.getPosition())
                        + "/"
                        + TextUtils.formatTime(at.getDuration())
                        + "]";

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(at.getInfo().author, null, null)
                .setTitle(at.getInfo().title)
                .setDescription(desc + "\n\nLoaded from " + at.getSourceManager().getSourceName())
                .setColor(new Color(28, 191, 226))
                .setFooter(channel.getJDA().getSelfUser().getName(), channel.getJDA().getSelfUser().getAvatarUrl())
                .build();

        channel.sendMessage(embed).queue();
    }

}
