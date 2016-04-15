package fredboat;

import fredboat.command.fun.AudioTrollCommand;
import fredboat.command.fun.DanceCommand;
import fredboat.command.util.AvatarCommand;
import fredboat.command.util.BrainfuckCommand;
import fredboat.commandmeta.CommandManager;
import fredboat.command.maintenance.DBGetCommand;
import fredboat.command.fun.EndTrollCommand;
import fredboat.command.maintenance.ExitCommand;
import fredboat.command.util.FindCommand;
import fredboat.command.util.HelpCommand;
import fredboat.command.fun.JokeCommand;
import fredboat.command.fun.LeetCommand;
import fredboat.command.fun.TextCommand;
import fredboat.command.mafia.MafiaStartCommand;
import fredboat.command.maintenance.EvalCommand;
import fredboat.command.util.LuaCommand;
import fredboat.command.maintenance.RestartCommand;
import fredboat.command.util.SayCommand;
import fredboat.command.maintenance.TestCommand;
import fredboat.command.maintenance.UptimeCommand;
import frederikam.jca.JCA;
import frederikam.jca.JCABuilder;
import java.io.InputStream;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.JDAInfo;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

public class FredBoat {

    public static final boolean IS_BETA = "Windows 10".equals(System.getProperty("os.name"));
    public static volatile JDA jda;
    public static JCA jca;
    public static final String PREFIX = IS_BETA ? "__" : ";;";
    public static final String OWNER_ID = "81011298891993088";
    public static final long START_TIME = System.currentTimeMillis();
    //public static final String ACCOUNT_EMAIL_KEY = IS_BETA ? "emailBeta" : "emailProduction";
    //public static final String ACCOUNT_PASSWORD_KEY = IS_BETA ? "passwordBeta" : "passwordProduction";
    public static final String ACCOUNT_TOKEN_KEY = IS_BETA ? "tokenBeta" : "tokenProduction";
    private static String accountToken;
    public static String CLIENT_ID = IS_BETA ? "168672778860494849" : "168686772216135681";
    //public static String accountEmail = IS_BETA ? "frederikmikkelsen2@outlook.com" : "frederikmikkelsen@outlook.com";
    //private static String accountPassword;
    public static String mashapeKey;
    public static String helpMsg = "Current commands:\n"
            + "```"
            + ";;help\n"
            + ";;say <text>\n"
            + ";;uptime or ;;stats\n"
            + ";;avatar @<username>\n"
            + ";;joke [name of main character]\n"
            + ";;lua <src> **experimental**\n"
            + ";;brainfuck <src> [input] **experimental**\n"
            + ";;leet <text>\n"
            + "```\n"
            + "Want to add FredBoat to your server? If you have Manage Server permissions for your guild, you can invite it here:\n"
            +"https://discordapp.com/oauth2/authorize?&client_id="+FredBoat.CLIENT_ID+"&scope=bot\n"
            + "You cannot send this bot commands though DM.\n"
            + "Bot created by Frederikam";
    public static String myUserId = "";
    public static volatile User myUser;

    public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException {
        //Load credentials file
        InputStream is = new FredBoat().getClass().getClassLoader().getResourceAsStream("credentials.json");
        Scanner scanner = new Scanner(is);
        JSONObject credsjson = new JSONObject(scanner.useDelimiter("\\A").next());
        
        //accountEmail = credsjson.getString(ACCOUNT_EMAIL_KEY);
        //accountPassword = credsjson.getString(ACCOUNT_PASSWORD_KEY);
        accountToken = credsjson.getString(ACCOUNT_TOKEN_KEY);
        mashapeKey = credsjson.getString("mashapeKey");
        String cbUser = credsjson.getString("cbUser");
        String cbKey = credsjson.getString("cbKey");
        
        if(credsjson.has("scopePasswords")){
            JSONObject scopePasswords = credsjson.getJSONObject("scopePasswords");
            for (String k : scopePasswords.keySet()){
                scopePasswords.put(k, scopePasswords.getString(k));
            }
        }
        
        scanner.close();
        
        fredboat.util.HttpUtils.init();
        jda = new JDABuilder().addListener(new ChannelListener()).setBotToken(accountToken).buildAsync();
        System.out.println("JDA version:\t"+JDAInfo.VERSION);
        
        //Initialise JCA
        jca = new JCABuilder().setKey(cbKey).setUser(cbUser).setNick("FredBoat").buildBlocking();
    }

    static void init() {
        if (IS_BETA) {
            helpMsg = helpMsg + "\n\n**This is the beta version of Fredboat. Are you sure you are not looking for the non-beta version \"FredBoat\"?**";
        }

        for (Guild guild : jda.getGuilds()) {
            System.out.println(guild.getName());

            for (TextChannel channel : guild.getTextChannels()) {
                System.out.println("\t" + channel.getName());
            }
        }

        myUserId = jda.getSelfInfo().getId();
        myUser = jda.getUserById(myUserId);

        //Commands
        CommandManager.registerCommand("help", new HelpCommand());
        CommandManager.registerCommand("dbget", new DBGetCommand());
        CommandManager.registerCommand("say", new SayCommand());
        CommandManager.registerCommand("uptime", new UptimeCommand());
        CommandManager.registerAlias("stats", "uptime");
        CommandManager.registerCommand("exit", new ExitCommand());
        CommandManager.registerCommand("avatar", new AvatarCommand());
        CommandManager.registerCommand("test", new TestCommand());
        CommandManager.registerCommand("lua", new LuaCommand());
        CommandManager.registerCommand("brainfuck", new BrainfuckCommand());
        CommandManager.registerCommand("joke", new JokeCommand());
        CommandManager.registerCommand("leet", new LeetCommand());
        CommandManager.registerAlias("1337", "leet");
        CommandManager.registerAlias("l33t", "leet");
        CommandManager.registerCommand("troll", new AudioTrollCommand());
        CommandManager.registerCommand("endtroll", new EndTrollCommand());
        CommandManager.registerCommand("restart", new RestartCommand());
        CommandManager.registerCommand("find", new FindCommand());
        CommandManager.registerCommand("dance", new DanceCommand());
        CommandManager.registerCommand("mafia", new MafiaStartCommand());
        CommandManager.registerAlias("startmafia", "mafia");
        CommandManager.registerAlias("werewolf", "mafia");
        CommandManager.registerAlias("startwerewolf", "mafia");
        CommandManager.registerCommand("eval", new EvalCommand());
        
        CommandManager.registerCommand("s", new TextCommand("¯\\_(ツ)_/¯"));
    }
}
