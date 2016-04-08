package fredboat.mafia;

import fredboat.FredBoat;
import fredboat.mafia.role.Alignment;
import fredboat.mafia.roleset.Roleset;
import fredboat.mafia.roleset.RolesetClassic;
import fredboat.util.TextUtils;
import fredboat.util.mafia.Election;
import fredboat.util.mafia.MafiaUtil;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.Region;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.PermissionOverride;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.managers.ChannelManager;
import net.dv8tion.jda.managers.GuildManager;
import net.dv8tion.jda.managers.PermissionOverrideManager;
import net.dv8tion.jda.utils.PermissionUtil;

public class MafiaGame extends Thread {

    public final Guild scumChatGuild;
    public final String SCUM_CHAT_GUILD_ID = "167885978227310592";

    public volatile MafiaGameStatus status = MafiaGameStatus.SETUP;
    public CopyOnWriteArrayList<MafiaPlayer> players = new CopyOnWriteArrayList<>();
    private final PlayerMessage initMsg;
    private LinkedBlockingQueue<PlayerMessage> queue;
    private final AtomicReference<JDA> jda;
    private User myUser;
    private boolean hasTemporaryChannels = false;
    private String gameName;
    private TextChannel townChannel;
    public TextChannel mafiaChannel;
    private Roleset roleset = new RolesetClassic();
    public int dayLength = 12 * 60 * 1000;
    public int nightLength = 4 * 60 * 1000;
    public int phase = 0;

    public MafiaGame(PlayerMessage initMsg, JDA jda, String name) {
        this.initMsg = initMsg;
        this.jda = new AtomicReference<>(jda);
        this.gameName = name;
        this.scumChatGuild = jda.getGuildById(SCUM_CHAT_GUILD_ID);
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

        if (initMsg.getMsg().getMentionedChannels().size() > 0) {
            if (initMsg.getPlayer().getId().equals(FredBoat.OWNER_ID) || PermissionUtil.checkPermission(initMsg.getPlayer(), Permission.MANAGE_CHANNEL, initMsg.getGuild())) {
                townChannel = initMsg.getMsg().getMentionedChannels().get(0);
            } else {
                TextUtils.replyWithMention((TextChannel) initMsg.getChannel(), initMsg.getPlayer(), " Error: Only users with Manage Channels can create games with a pre-existing channel! Try again without specifying channel.");
                shutdown();
                return;
            }
        }

        String msg = " Now starting game setup with name `" + gameName + "`.\n";
        //        + "Say " + FredBoat.PREFIX + "endsetup to stop setting up this game.\n"
        //        + "First you must configure where the game will be held.\n";

        if (townChannel != null) {
            if (PermissionUtil.checkPermission(FredBoat.myUser, Permission.MANAGE_PERMISSIONS, townChannel)) {
                msg = msg + "Selected town channel is " + new MessageBuilder().appendMention(townChannel).build().getRawContent();
            } else {
                TextUtils.replyWithMention((TextChannel) initMsg.getChannel(), initMsg.getPlayer(), " Sorry, I don't have permission to Manage Roles in " + new MessageBuilder().appendMention(townChannel).build().getRawContent());
                shutdown();
                return;
            }
        } else {
            hasTemporaryChannels = true;
            //Check for MANAGE_PERMISSIONS
            if (PermissionUtil.checkPermission(FredBoat.myUser, Permission.MANAGE_PERMISSIONS, initMsg.getGuild()) == false) {
                TextUtils.replyWithMention((TextChannel) initMsg.getChannel(), initMsg.getPlayer(), " Sorry, I need global Manage Permissions to start a game in a temporary channel! Try tagging a channel intended for Mafia games.");
                shutdown();
                return;
            }
        }

        TextUtils.replyWithMention((TextChannel) initMsg.getChannel(), initMsg.getPlayer(), msg);

        try {
            /*
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
            }*/

            status = MafiaGameStatus.REGISTRATION;

            MessageBuilder mb = new MessageBuilder();
            mb.appendString("Registration is now active. Say ")
                    .appendString(FredBoat.PREFIX + "register " + gameName, MessageBuilder.Formatting.BLOCK)
                    .appendString(" to participate.\nSay ")
                    .appendString(FredBoat.PREFIX + "unregister", MessageBuilder.Formatting.BLOCK)
                    .appendString("to unregister.");
            initMsg.getChannel().sendMessage(mb.build());

            printRegistrationList(initMsg.getChannel());

            while (players.size() < roleset.getSize()) {
                PlayerMessage newMsg = queue.take();
                if (newMsg.getMsg().getContent().equalsIgnoreCase(FredBoat.PREFIX + "register " + gameName)) {
                    if (MafiaGameRegistry.isPlayerAlreadyInGame(newMsg.getPlayer())) {
                        TextUtils.replyWithMention((TextChannel) newMsg.getChannel(), newMsg.getPlayer(), " You are already in a game!");
                    } else {
                        players.add(newMsg.getPlayer());
                        printRegistrationList(newMsg.getChannel());
                    }
                } else if (newMsg.getMsg().getContent().toLowerCase().startsWith(FredBoat.PREFIX + " unregister")) {
                    if (players.remove(newMsg.getPlayer())) {
                        printRegistrationList(newMsg.getChannel());//Successfully removed!
                    }
                    if (players.isEmpty()) {
                        newMsg.getChannel().sendMessage("Player list is empty. Ending setup.");
                        shutdown();
                        return;
                    }
                }
            }

            initMsg.getChannel().sendMessage("Now setting up game and generating channels. This may take some time because of rate limiting...");
            /*final CountDownLatch guildCompletionLatch = new CountDownLatch(1);

            //Generate guilds/channels
            jda.get().createGuildAsync("Temporary Mafia Guild: " + gameName, Region.AMSTERDAM, (GuildManager t) -> {
                tempGameGuildManager = t;
                guildCompletionLatch.countDown();
            });

            guildCompletionLatch.await();
            for (VoiceChannel vc : scumGuildManager.getGuild().getVoiceChannels()) {
                vc.getManager().delete();
            }

            

            //Set guild perms for @everyone
            PermissionOverrideManager pom = mafiaChannel.createPermissionOverride(mafiaChannel.getGuild().getPublicRole());
            pom.deny(Permission.CREATE_INSTANT_INVITE);
            pom.update();*/

            mafiaChannel = (TextChannel) scumChatGuild.createTextChannel("mafia_channel_" + gameName.replace(' ', '_')).getChannel();
            mafiaChannel.getManager()
                    .setTopic("Private chat for members of the mafia.")
                    .update();

            MafiaUtil.refreshAllMafiaChatPermissions(this);

            roleset.assignRoles(this, players);

            for (MafiaPlayer plr : players) {
                plr.gameRole.sendRolePM(plr, this);
            }

            ChannelManager cm;
            if (townChannel == null) {
                cm = initMsg.getGuild().createTextChannel("mafia_game_" + gameName.replace(' ', '_'));
                townChannel = (TextChannel) cm.getChannel();
            } else {
                cm = townChannel.getManager();
            }

            //Clear all permissions from the channel
            for (PermissionOverride po : townChannel.getPermissionOverrides()) {
                try {
                    po.getManager().delete();
                } catch (Exception ex) {
                    //ignore
                }
            }

            //Deny the right to speak for non-players
            PermissionOverrideManager pomPublic;
            if (townChannel.getOverrideForRole(townChannel.getGuild().getPublicRole()) == null) {
                pomPublic = townChannel.createPermissionOverride(townChannel.getGuild().getPublicRole());
            } else {
                pomPublic = townChannel.getOverrideForRole(townChannel.getGuild().getPublicRole()).getManager();
            }
            pomPublic.deny(Permission.MESSAGE_WRITE)
                    .update();

            //Now allow the right to speak for players
            for (MafiaPlayer plr : players) {
                townChannel.createPermissionOverride(plr)
                        .grant(Permission.MESSAGE_WRITE)
                        .update();
            }

            cm.update();

            //Start the game
            Message startMsg = new MessageBuilder().appendString("**Welcome to a game of Mafia (aka Werewolf)!**\n"
                    + "You may use the following commands:")
                    .appendCodeBlock(
                            ":vote [player]\n"
                            + ":vote no lynch\n"
                            + ":unvote", "")
                    .appendString("Majority lynch is disabled. The phase will end when the time runs out or everyone has voted.")
                    .build();
            townChannel.sendMessage(startMsg);
            onDayStart();

            //TextUtils.replyWithMention((TextChannel) initMsg.getChannel(), initMsg.getPlayer(), " Attempting to create new channels...");
        } catch (InterruptedException ex) {
            System.out.println("Game " + gameName + " got interrupted.");
            shutdown();
            return;
        } catch (Exception ex) {
            TextUtils.handleException(ex, initMsg.getChannel(), initMsg.getPlayer());
            shutdown();
            return;
        }

        shutdown();//Reached end of function
    }

    private void onDayStart() throws InterruptedException {
        checkForGameEnded();
        status = MafiaGameStatus.DAY;
        phase = phase + 1;
        printAlivePlayersList();

        for (MafiaPlayer plr : players) {
            if (plr.status == MafiaPlayerStatus.ALIVE) {
                townChannel.getOverrideForUser(plr).getManager().grant(Permission.MESSAGE_WRITE);
                sleep(1000);//Negates rate limiting
            }

            ArrayList<MafiaPlayer> alivePlayers = getAlivePlayers();
            Election lynchVotes = new Election(alivePlayers);
        }
    }

    private void onNightStart() throws InterruptedException {
        checkForGameEnded();
        status = MafiaGameStatus.NIGTH;
        MafiaUtil.refreshAllMafiaChatPermissions(this);
        for (MafiaPlayer plr : players) {
            townChannel.getOverrideForUser(plr).getManager().deny(Permission.MESSAGE_WRITE);
            sleep(1000);//Negates rate limiting
        }
    }

    private void checkForGameEnded() throws InterruptedException {
        ArrayList<MafiaPlayer> mafia = getAlivePlayersByAlignment(Alignment.MAFIA);
        ArrayList<MafiaPlayer> town = getAlivePlayersByAlignment(Alignment.TOWNIES);

        if (mafia.size() >= town.size()) {
            onMafiaWin();
        } else if (mafia.isEmpty()) {
            onTownWin();
        }
    }

    private void onMafiaWin() throws InterruptedException {
        status = MafiaGameStatus.ENDED;

        MessageBuilder b = new MessageBuilder();
        b.appendString("__The village has been eliminated. The **Mafia** wins! Members of the mafia:__\n");
        for (MafiaPlayer plr : getAlivePlayersByAlignment(Alignment.MAFIA)) {
            b.appendString("\n")
                    .appendMention(plr);
        }
        b.appendString("\n\nPost-game discussion starts now.");
        Message msg = b.build();
        townChannel.sendMessage(b.build());

        players.clear();
        wait(60*5*100);
        
        shutdown();
    }

    private void onTownWin() throws InterruptedException {
        status = MafiaGameStatus.ENDED;
        shutdown();
    }

    public ArrayList<MafiaPlayer> getPlayersByAlignment(Alignment align) {
        ArrayList<MafiaPlayer> plrs = new ArrayList<>();
        for (MafiaPlayer plr : plrs) {
            if (plr.gameRole.getAlignment() == align) {
                plrs.add(plr);
            }
        }
        return plrs;
    }

    public ArrayList<MafiaPlayer> getAlivePlayersByAlignment(Alignment align) {
        ArrayList<MafiaPlayer> plrs = getAlivePlayers();
        for (MafiaPlayer plr : plrs) {
            if (plr.gameRole.getAlignment() == align) {
                plrs.add(plr);
            }
        }
        return plrs;
    }

    private Message printRegistrationList(MessageChannel channel) {
        return printRegistrationList(channel, true);
    }

    private Message printRegistrationList(MessageChannel channel, boolean doSend) {
        MessageBuilder b = new MessageBuilder();
        b.appendString("Registration list for " + gameName + ":", MessageBuilder.Formatting.UNDERLINE)
                .appendString("\n");
        for (int i = 0; i < roleset.getSize(); i++) {
            if (i < players.size()) {
                b.appendString("\n   `" + (i + 1) + ".`  ").appendString(players.get(i).getUsername(), MessageBuilder.Formatting.BOLD);
            } else {
                b.appendString("\n   `" + (i + 1) + ".`  ").appendString("Vacant", MessageBuilder.Formatting.ITALICS);
            }
        }
        Message msg = b.build();
        if (doSend) {
            channel.sendMessage(b.build());
        }
        return msg;
    }

    private Message printAlivePlayersList() {
        return printAlivePlayersList(true);
    }

    private Message printAlivePlayersList(boolean doSend) {
        MessageBuilder b = new MessageBuilder();
        b.appendString("**__Alive players `D" + phase + "`:__**\n");
        for (MafiaPlayer plr : players) {
            b.appendString("\n")
                    .appendMention(plr);
        }
        b.appendString("\n\n**IT IS DAY. YOU MAY POST.**");
        Message msg = b.build();
        if (doSend) {
            townChannel.sendMessage(b.build());
        }
        return msg;
    }

    public ArrayList<MafiaPlayer> getAlivePlayers() {
        ArrayList<MafiaPlayer> alive = new ArrayList<>();

        for (MafiaPlayer plr : players) {
            if (plr.status == MafiaPlayerStatus.ALIVE) {
                alive.add(plr);
            }
        }

        return alive;
    }

    public void shutdown() {
        MafiaGameRegistry.activeGames.remove(this);
        status = MafiaGameStatus.ENDED;
        if (hasTemporaryChannels) {
            try {
                townChannel.getManager().delete();
                mafiaChannel.getManager().delete();
            } catch (Exception ex) {
            }
        }
        interrupt();
    }
}
