package fredboat;

import fredboat.command.fun.DanceCommand;
import fredboat.command.util.AvatarCommand;
import fredboat.command.util.BrainfuckCommand;
import fredboat.command.maintenance.DBGetCommand;
import fredboat.command.maintenance.ExitCommand;
import fredboat.command.util.FindCommand;
import fredboat.command.util.HelpCommand;
import fredboat.command.fun.JokeCommand;
import fredboat.command.fun.LeetCommand;
import fredboat.command.fun.PatCommand;
import fredboat.command.fun.RemoteFileCommand;
import fredboat.command.fun.RiotCommand;
import fredboat.command.fun.TalkCommand;
import fredboat.command.fun.TextCommand;
import fredboat.command.mafia.MafiaStartCommand;
import fredboat.command.maintenance.EvalCommand;
import fredboat.command.util.LuaCommand;
import fredboat.command.maintenance.RestartCommand;
import fredboat.command.util.SayCommand;
import fredboat.command.maintenance.TestCommand;
import fredboat.command.maintenance.UptimeCommand;
import fredboat.command.util.ClearCommand;
import fredboat.command.util.DumpCommand;
import fredboat.command.util.UpdateCommand;
import fredboat.commandmeta.CommandRegistry;
import fredboat.event.EventListenerBoat;
import fredboat.event.EventListenerSelf;
import frederikam.jca.JCA;
import frederikam.jca.JCABuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.JDAInfo;
import net.dv8tion.jda.client.JDAClientBuilder;
import net.dv8tion.jda.entities.User;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

public class FredBoat {

    public static final boolean IS_BETA = System.getProperty("os.name").toLowerCase().contains("windows");
    public static volatile JDA jdaBot;
    public static volatile JDA jdaSelf;
    public static JCA jca;
    public static final String PREFIX = IS_BETA ? "¤" : ";;";
    public static final String SELF_PREFIX = IS_BETA ? "::" : "<<";
    public static final String OWNER_ID = "81011298891993088";
    public static Jedis jedis;
    public static final long START_TIME = System.currentTimeMillis();
    //public static final String ACCOUNT_EMAIL_KEY = IS_BETA ? "emailBeta" : "emailProduction";
    //public static final String ACCOUNT_PASSWORD_KEY = IS_BETA ? "passwordBeta" : "passwordProduction";
    public static final String ACCOUNT_TOKEN_KEY = IS_BETA ? "tokenBeta" : "tokenProduction";
    private static String accountToken;
    public static String CLIENT_ID = IS_BETA ? "168672778860494849" : "168686772216135681";
    //public static String accountEmail = IS_BETA ? "frederikmikkelsen2@outlook.com" : "frederikmikkelsen@outlook.com";
    //private static String accountPassword;
    public static String mashapeKey;
    public static String helpMsg = "";

    public static String myUserId = "";
    public static volatile User myUser;

    public static int readyEvents = 0;
    public static final int READY_EVENTS_REQUIRED = 2;

    public static EventListenerBoat listenerBot;
    public static EventListenerSelf listenerSelf;

    public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, IOException {
        //Load credentials file
        FredBoat instance = new FredBoat();
        InputStream is = new FileInputStream(new File("./credentials.json"));
        //InputStream is = instance.getClass().getClassLoader().getResourceAsStream("credentials.json");
        Scanner scanner = new Scanner(is);
        JSONObject credsjson = new JSONObject(scanner.useDelimiter("\\A").next());

        InputStream helpIS = instance.getClass().getClassLoader().getResourceAsStream("help.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(helpIS));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            helpMsg = helpMsg + inputLine + "\n";
        }
        in.close();
        helpMsg = helpMsg.replace("%CLIENT_ID%", CLIENT_ID);
        helpMsg = helpMsg.replace("%SHRUG%", "¯\\_(ツ)_/¯");

        //accountEmail = credsjson.getString(ACCOUNT_EMAIL_KEY);
        //accountPassword = credsjson.getString(ACCOUNT_PASSWORD_KEY);
        accountToken = credsjson.getString(ACCOUNT_TOKEN_KEY);
        mashapeKey = credsjson.getString("mashapeKey");
        String cbUser = credsjson.getString("cbUser");
        String cbKey = credsjson.getString("cbKey");
        String accountEmail = credsjson.getString("email");
        String accountPassword = credsjson.getString("password");
        String redisPassword = credsjson.getString("redisPassword");

        if (credsjson.has("scopePasswords")) {
            JSONObject scopePasswords = credsjson.getJSONObject("scopePasswords");
            for (String k : scopePasswords.keySet()) {
                scopePasswords.put(k, scopePasswords.getString(k));
            }
        }

        scanner.close();

        //Initialise event listeners
        listenerBot = new EventListenerBoat(0x01, PREFIX);
        listenerSelf = new EventListenerSelf(0x10, SELF_PREFIX);

        fredboat.util.HttpUtils.init();
        jdaBot = new JDABuilder().addListener(listenerBot).setBotToken(accountToken).buildAsync();
        jdaSelf = new JDAClientBuilder().addListener(listenerSelf).setEmail(accountEmail).setPassword(accountPassword).buildAsync();
        System.out.println("JDA version:\t" + JDAInfo.VERSION);

        //Initialise JCA
        jca = new JCABuilder().setKey(cbKey).setUser(cbUser).buildBlocking();
        
        jedis = new Jedis("frednet3.revgamesrblx.com", 6379);
        jedis.auth(redisPassword);
    }

    public static void init() {
        readyEvents = readyEvents + 1;

        System.out.println("INIT: " + readyEvents);

        if (readyEvents < READY_EVENTS_REQUIRED) {
            return;
        }

        if (IS_BETA) {
            helpMsg = helpMsg + "\n\n**This is the beta version of Fredboat. Are you sure you are not looking for the non-beta version \"FredBoat\"?**";
        }

        /*for (Guild guild : jdaBot.getGuilds()) {
            System.out.println(guild.getName());

            for (TextChannel channel : guild.getTextChannels()) {
                System.out.println("\t" + channel.getName());
            }
        }*/

        myUserId = jdaBot.getSelfInfo().getId();
        myUser = jdaBot.getUserById(myUserId);

        //Commands
        CommandRegistry.registerCommand(0x01, "help", new HelpCommand());
        CommandRegistry.registerCommand(0x11, "dbget", new DBGetCommand());
        CommandRegistry.registerCommand(0x11, "say", new SayCommand());
        CommandRegistry.registerCommand(0x11, "uptime", new UptimeCommand());
        CommandRegistry.registerAlias("uptime", "stats");
        CommandRegistry.registerCommand(0x11, "exit", new ExitCommand());
        CommandRegistry.registerCommand(0x11, "avatar", new AvatarCommand());
        CommandRegistry.registerCommand(0x11, "test", new TestCommand());
        CommandRegistry.registerCommand(0x11, "lua", new LuaCommand());
        CommandRegistry.registerCommand(0x11, "brainfuck", new BrainfuckCommand());
        CommandRegistry.registerCommand(0x11, "joke", new JokeCommand());
        CommandRegistry.registerCommand(0x11, "leet", new LeetCommand());
        CommandRegistry.registerAlias("leet", "1337");
        CommandRegistry.registerAlias("leet", "l33t");
        CommandRegistry.registerCommand(0x11, "riot", new RiotCommand());
        CommandRegistry.registerCommand(0x11, "update", new UpdateCommand());
        //CommandRegistry.registerCommand(0x11, "troll", new AudioTrollCommand());
        //CommandRegistry.registerCommand(0x11, "endtroll", new EndTrollCommand());
        CommandRegistry.registerCommand(0x11, "restart", new RestartCommand());
        CommandRegistry.registerCommand(0x11, "find", new FindCommand());
        CommandRegistry.registerCommand(0x11, "dance", new DanceCommand());
        CommandRegistry.registerCommand(0x11, "mafia", new MafiaStartCommand());
        CommandRegistry.registerAlias("mafia", "startmafia");
        CommandRegistry.registerAlias("mafia", "werewolf");
        CommandRegistry.registerAlias("mafia", "startwerewolf");
        CommandRegistry.registerCommand(0x11, "eval", new EvalCommand());
        CommandRegistry.registerCommand(0x11, "s", new TextCommand("¯\\_(ツ)_/¯"));
        CommandRegistry.registerCommand(0x11, "lenny", new TextCommand("( ͡° ͜ʖ ͡°)"));
        CommandRegistry.registerCommand(0x11, "clear", new ClearCommand());
        CommandRegistry.registerCommand(0x11, "talk", new TalkCommand());
        CommandRegistry.registerCommand(0x11, "dump", new DumpCommand());
        
        //Begin sergi memes//
        
        CommandRegistry.registerCommand(0x11, "welcome", new RemoteFileCommand("https://cdn.discordapp.com/attachments/132490115137142784/176587676093251585/2f16d4b6d0.png"));
        CommandRegistry.registerCommand(0x11, "rude", new RemoteFileCommand("http://i.imgur.com/pUn7ijx.png"));
        CommandRegistry.registerCommand(0x11, "fuck", new RemoteFileCommand("http://i.imgur.com/1bllKNh.png"));
        CommandRegistry.registerCommand(0x11, "idc", new RemoteFileCommand("http://i.imgur.com/0ZPjpNg.png"));
        CommandRegistry.registerCommand(0x11, "beingraped", new RemoteFileCommand("http://i.imgur.com/dPsYRYV.png"));
        CommandRegistry.registerCommand(0x11, "anime", new RemoteFileCommand("https://cdn.discordapp.com/attachments/132490115137142784/177751190333816834/animeeee.png"));
        CommandRegistry.registerCommand(0x11, "wow", new RemoteFileCommand("http://img.prntscr.com/img?url=http://i.imgur.com/aexiMAG.png"));
        CommandRegistry.registerCommand(0x11, "what", new RemoteFileCommand("http://i.imgur.com/CTLraK4.png"));
        CommandRegistry.registerCommand(0x11, "pun", new RemoteFileCommand("http://i.imgur.com/2hFMrjt.png"));
        CommandRegistry.registerCommand(0x11, "suicide", new RemoteFileCommand("http://nekomata.moe/i/S3g6D0.png"));
        CommandRegistry.registerCommand(0x11, "die", new RemoteFileCommand("http://nekomata.moe/i/k2B0f6.png"));
        CommandRegistry.registerCommand(0x11, "stupid", new RemoteFileCommand("http://nekomata.moe/i/c056y7.png"));
        CommandRegistry.registerCommand(0x11, "stop", new RemoteFileCommand("http://puu.sh/oQN2j/8e09872842.jpg"));
        CommandRegistry.registerAlias("stop", "cancer");
        CommandRegistry.registerCommand(0x11, "stupidbot", new RemoteFileCommand("https://cdn.discordapp.com/attachments/143976784545841161/183171963399700481/unknown.png"));
        CommandRegistry.registerCommand(0x11, "escape", new RemoteFileCommand("http://i.imgur.com/kk7Zu3C.png"));
        CommandRegistry.registerCommand(0x11, "explosion", new RemoteFileCommand("https://cdn.discordapp.com/attachments/143976784545841161/182893975965794306/megumin7.gif"));
        CommandRegistry.registerCommand(0x11, "gif", new RemoteFileCommand("https://cdn.discordapp.com/attachments/132490115137142784/182907929765085185/spacer.gif"));
        
        String[] pats = {
            "http://i.imgur.com/wF1ohrH.gif",
            "http://cdn.photonesta.com/images/i.imgur.com/I3yvqFL.gif",
            "http://i4.photobucket.com/albums/y131/quentinlau/Blog/sola-02-Large15.jpg",
            "http://i.imgur.com/OYiSZWX.gif",
            "https://cdn.discordapp.com/attachments/132490115137142784/182502254400503809/tumblr_nb0gnw9qgh1tbewqgo1_500.gif",
            "https://cdn.discordapp.com/attachments/132490115137142784/182502347493212161/tumblr_nb8orpea161qa94xto1_500.gif",
            "http://31.media.tumblr.com/e759f2da1f07de37832fc8269e99f1e7/tumblr_n3w02z954N1swm6rso1_500.gif",
            "https://media1.giphy.com/media/ye7OTQgwmVuVy/200.gif",
            "http://data.whicdn.com/images/224314340/large.gif",
            "https://cdn.discordapp.com/attachments/143976784545841161/182504493760380930/tumblr_m24qp6UAfe1qcyi3ao1_500.gif",
            "https://cdn.discordapp.com/attachments/143976784545841161/182505413982289922/cc9d7b5849745daff691a0b7c4bb9d23.jpg",
            "http://i.imgur.com/eOJlnwP.gif",
            "http://i.imgur.com/i7bklkm.gif",
        };
        CommandRegistry.registerCommand(0x11, "pat", new PatCommand(pats));
    }
    
    public static void shutdown(int code){
        jdaBot.shutdown(true);
        jdaSelf.shutdown(true);
        jedis.shutdown();
        System.exit(code);
    }
}
