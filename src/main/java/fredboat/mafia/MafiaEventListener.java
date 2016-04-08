package fredboat.mafia;

import fredboat.mafia.role.Alignment;
import fredboat.util.mafia.MafiaUtil;
import java.util.concurrent.LinkedBlockingQueue;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.impl.JDAImpl;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.PermissionUtil;

public class MafiaEventListener extends ListenerAdapter {

    public MafiaGame game;
    public LinkedBlockingQueue<PlayerMessage> queue;

    public MafiaEventListener(MafiaGame game) {
        this.game = game;
        queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MafiaPlayer plr = game.getPlayerFromUser(event.getAuthor());
        if (plr != null || game.status == MafiaGameStatus.REGISTRATION) {
            if (plr == null) {
                plr = new MafiaPlayer(event.getAuthor().getId(), (JDAImpl) event.getJDA());
            }
            PlayerMessage msg = new PlayerMessage(event.getMessage(), plr, event.getChannel(), event.getGuild());
            queue.add(msg);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getGuild() == game.scumChatGuild) {
            MafiaPlayer plr = game.getPlayerFromUser(event.getUser());
            if (plr != null) {
                MafiaUtil.refreshMafiaChatPermssions(game, plr);
            }
        }
    }

    public LinkedBlockingQueue<PlayerMessage> getQueue() {
        return queue;
    }

}
