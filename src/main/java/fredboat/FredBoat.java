package fredboat;

import fredboat.command.AvatarCommand;
import fredboat.command.BrainfuckCommand;
import fredboat.command.meta.CommandManager;
import fredboat.command.DBGetCommand;
import fredboat.command.ExitCommand;
import fredboat.command.HelpCommand;
import fredboat.command.JokeCommand;
import fredboat.command.LeetCommand;
import fredboat.command.LuaCommand;
import fredboat.command.SayCommand;
import fredboat.command.TestCommand;
import fredboat.command.UptimeCommand;
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
    public static JDA jda;
    public static final String PREFIX = IS_BETA ? "¤" : ";;";
    public static final String OWNER_ID = "81011298891993088";
    public static final long START_TIME = System.currentTimeMillis();
    public static final String ACCOUNT_EMAIL_KEY = IS_BETA ? "emailBeta" : "emailProduction";
    public static final String ACCOUNT_PASSWORD_KEY = IS_BETA ? "passwordBeta" : "passwordProduction";
    public static String accountEmail = IS_BETA ? "frederikmikkelsen2@outlook.com" : "frederikmikkelsen@outlook.com";
    private static String accountPassword;
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
            + "Want to add FredBoat to your server? Send it an invite link!\n"
            + "You cannot send this bot commands though DM.\n"
            + "Bot created by Frederikam";
    public static String myUserId = "";
    public static User myUser;

    public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException {
        //Load credentials file
        InputStream is = new FredBoat().getClass().getClassLoader().getResourceAsStream("credentials.json");
        Scanner scanner = new Scanner(is);
        JSONObject credsjson = new JSONObject(scanner.useDelimiter("\\A").next());
        
        accountEmail = credsjson.getString(ACCOUNT_EMAIL_KEY);
        accountPassword = credsjson.getString(ACCOUNT_PASSWORD_KEY);
        mashapeKey = credsjson.getString("mashapeKey");
        
        if(credsjson.has("scopePasswords")){
            JSONObject scopePasswords = credsjson.getJSONObject("scopePasswords");
            for (String k : scopePasswords.keySet()){
                scopePasswords.put(k, scopePasswords.getString(k));
            }
        }
        
        scanner.close();
        
        fredboat.util.HttpUtils.init();
        jda = new JDABuilder().addListener(new ChannelListener()).setEmail(accountEmail).setPassword(accountPassword).buildAsync();
        System.out.println("JDA version:\t"+JDAInfo.VERSION);
    }

    static void init() {
        if (IS_BETA) {
            helpMsg = helpMsg + "\n**This is the beta version of Fredboat. Are you sure you are not looking for the non-beta version \"FredBoat\"?**";
        }

        for (Guild guild : jda.getGuilds()) {
            System.out.println(guild.getName());

            for (TextChannel channel : guild.getTextChannels()) {
                System.out.println("\t" + channel.getName());
            }
        }

        myUser = jda.getUserById(myUserId);
        myUserId = jda.getSelfInfo().getId();

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
    }
}
