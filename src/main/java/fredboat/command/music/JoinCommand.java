package fredboat.command.music;

import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import net.dv8tion.jda.entities.*;

public class JoinCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild.getId());
        VoiceChannel vc = player.getUserCurrentVoiceChannel(invoker);
        player.setCurrentTC(channel);
        try {
            player.joinChannel(vc);
            if (vc != null) {
                channel.sendMessage("Joining " + vc.getName());
            }
        } catch (IllegalStateException ex) {
            channel.sendMessage("An error occurred. Couldn't join " + player.getChannel().getName() + " because I am already trying to connect to that channel. Please try again.");
        }
    }

}
