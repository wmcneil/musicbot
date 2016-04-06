package fredboat.mafia;

import fredboat.FredBoat;
import fredboat.util.TextUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.utils.PermissionUtil;

public class MafiaGame extends Thread {

    public volatile MafiaGameStatus status = MafiaGameStatus.SETUP;
    public CopyOnWriteArrayList<MafiaPlayer> players = new CopyOnWriteArrayList<>();
    private final PlayerMessage initMsg;
    private LinkedBlockingQueue<PlayerMessage> queue;
    private final AtomicReference<JDA> jda;
    private User myUser;
    private boolean hasTemporaryChannels = false;
    private String gameName;
    private TextChannel townChannel;
    private TextChannel mafiaChannel;

    public MafiaGame(PlayerMessage initMsg, JDA jda, String name) {
        this.initMsg = initMsg;
        this.jda = new AtomicReference<>(jda);
        this.gameName = name;
    }

    public MafiaPlayer getPlayerFromUser(User usr) {
        for (MafiaPlayer plr : players) {
            if (plr.getId().equals(usr.getId())) {
                return plr;
            }
        }
        return null;
    }

    @Override
    public void run() {
        myUser = jda.get().getUserById(jda.get().getSelfInfo().getId());

        //Insert the host to the players list
        players.add(initMsg.getPlayer());

        //Initialise event handler
        MafiaEventListener listener = new MafiaEventListener(this);
        queue = listener.getQueue();
        jda.get().addEventListener(listener);
        MafiaGameRegistry.add(this);

        //Check for Manage Channels
        if (PermissionUtil.checkPermission(FredBoat.myUser, Permission.MANAGE_CHANNEL, initMsg.getGuild()) == false) {
            TextUtils.replyWithMention((TextChannel) initMsg.getChannel(), initMsg.getPlayer(), " Sorry, I need the Manage Channels permission to start a game!");
            shutdown();
            return;
        }

        TextUtils.replyWithMention((TextChannel) initMsg.getChannel(), initMsg.getPlayer(), " Now starting game setup with name " + gameName + ".\n"
                + "Say " + FredBoat.PREFIX + "endsetup to stop setting up this game.\n"
                + "First you must configure where the game will be held.\n"
                + "Would you like me to generate a temporary channel? Y/n");

        try {
            boolean repeat = true;
            while (repeat) {
                PlayerMessage newMsg = queue.take();
                if (newMsg.getMsg().getContent().equalsIgnoreCase("y")) {
                    hasTemporaryChannels = true;
                    repeat = false;
                } else if (newMsg.getMsg().getContent().equalsIgnoreCase("n")) {
                    TextUtils.replyWithMention((TextChannel) initMsg.getChannel(), initMsg.getPlayer(), " n: It is not currently possible to use preexisting channels. Exiting...");
                    shutdown();
                    return;
                }
            }
            
            MessageBuilder mb = new MessageBuilder();
            mb.appendString("Registration is now active. Say ")
                    .appendString(FredBoat.PREFIX+";;register "+gameName, MessageBuilder.Formatting.BLOCK)
                    .appendString(" to participate.\nSay ")
                    .appendString(FredBoat.PREFIX+";;unregister", MessageBuilder.Formatting.BLOCK)
                    .appendString("to unregister.");//todo
            
            status = MafiaGameStatus.REGISTRATION;
            
            //TextUtils.replyWithMention((TextChannel) initMsg.getChannel(), initMsg.getPlayer(), " Attempting to create new channels...");
            

        } catch (InterruptedException ex) {
            shutdown();
            return;
        }

        shutdown();//Reached end of function
    }

    private void printRegistrationList(TextChannel channel){
        MessageBuilder b = new MessageBuilder();
        b.appendString("Registration list for "+gameName, MessageBuilder.Formatting.BOLD);
        int i = 0;
        for(MafiaPlayer p : players){
            i++;
            b.appendString(i+". "+p.getUsername());
        }
        
        channel.sendMessage(b.build());
    }
    
    public void shutdown() {
        MafiaGameRegistry.activeGames.remove(this);
        if(hasTemporaryChannels){
            try{
                townChannel.getManager().delete();
                mafiaChannel.getManager().delete();
            }catch(Exception ex){}
        }
        interrupt();
    }
}
