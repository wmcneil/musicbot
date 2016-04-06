package fredboat.mafia;

import java.util.concurrent.LinkedBlockingQueue;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

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
        if (plr != null) {
            System.out.println("Queuing: "+event.getMessage().getContent());
            PlayerMessage msg = new PlayerMessage(event.getMessage(), plr, event.getChannel(), event.getGuild());
            queue.add(msg);
        }
    }

    public LinkedBlockingQueue<PlayerMessage> getQueue() {
        return queue;
    }

}
