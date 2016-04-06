package fredboat.command.mafia;

import fredboat.commandmeta.Command;
import fredboat.mafia.MafiaGame;
import fredboat.mafia.MafiaGameRegistry;
import fredboat.mafia.MafiaPlayer;
import fredboat.mafia.PlayerMessage;
import fredboat.util.TextUtils;
import java.util.UUID;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.impl.JDAImpl;

public class MafiaStartCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        if (MafiaGameRegistry.isPlayerAlreadyInGame(invoker)) {
            TextUtils.replyWithMention(channel, invoker, " Cant do that, you are already in a game!");
        } else {
            MafiaPlayer plr = new MafiaPlayer(invoker.getId(), (JDAImpl) guild.getJDA());
            PlayerMessage initMsg = new PlayerMessage(message, plr, channel, guild);
            String gamename = args.length > 1 ? args[1] : UUID.randomUUID().toString().substring(0,7);
            MafiaGame game = new MafiaGame(initMsg, guild.getJDA(), gamename);
            game.start();
        }
    }
}
