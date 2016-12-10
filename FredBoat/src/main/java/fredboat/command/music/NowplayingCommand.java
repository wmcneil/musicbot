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

import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack;
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
            } else {
                sendDefaultResponse(channel, at);
            }

        } else {
            channel.sendMessage("Not currently playing anything.").queue();
        }
    }

    private void sendYoutubeEmbed(TextChannel channel, YoutubeAudioTrack at){
        YoutubeVideo yv = YoutubeAPI.getVideoFromID(at.getIdentifier(), true);

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(at.getInfo().title)
                .setUrl("https://www.youtube.com/watch?v=" + at.getIdentifier())
                .setDescription("["
                        + TextUtils.formatTime(at.getPosition())
                        + "/"
                        + TextUtils.formatTime(at.getDuration())
                        + "]\n\n" + yv.getDescription())
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
                        + "]\n\n") //TODO: Gather description, thumbnail, etc
                .setColor(new Color(255, 85, 0))
                .setFooter(channel.getJDA().getSelfUser().getName(), channel.getJDA().getSelfUser().getAvatarUrl())
                .build();

        channel.sendMessage(embed).queue();
    }

    private void sendDefaultResponse(TextChannel channel, AudioTrack at){
        channel.sendMessage("Now playing " + at.getInfo().title + " ["
                + TextUtils.formatTime(at.getPosition())
                + "/"
                + TextUtils.formatTime(at.getDuration())
                + "]").queue();
    }

}
