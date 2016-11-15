package fredboat.util;

import fredboat.FredBoat;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.impl.JDAImpl;
import net.dv8tion.jda.requests.Requester;
import org.json.JSONObject;

import java.util.List;

public class DiscordUtil {

    private DiscordUtil() {
    }

    public static boolean isMainBot() {
        return (FredBoat.getScopes() & 0x100) != 0;
    }

    public static boolean isMusicBot() {
        return (FredBoat.getScopes() & 0x010) != 0;
    }

    public static boolean isSelfBot() {
        return (FredBoat.getScopes() & 0x001) != 0;
    }

    public static boolean isMainBotPresent(Guild guild) {
        JDA jda = guild.getJDA();
        User other = jda.getUserById(BotConstants.MAIN_BOT_ID);
        return guild.getUsers().contains(other);
    }

    public static boolean isMusicBotPresent(Guild guild) {
        JDA jda = guild.getJDA();
        User other = jda.getUserById(BotConstants.MUSIC_BOT_ID);
        return guild.getUsers().contains(other);
    }
    
    public static boolean isPatronBotPresentAndOnline(Guild guild) {
        JDA jda = guild.getJDA();
        User other = jda.getUserById(BotConstants.PATRON_BOT_ID);
        return guild.getUsers().contains(other) && other.getOnlineStatus() == OnlineStatus.ONLINE;
    }

    public static boolean isUserBotCommander(Guild guild, User user) {
        List<Role> roles = guild.getRolesForUser(user);

        for (Role r : roles) {
            if (r.getName().equals("Bot Commander")) {
                return true;
            }
        }

        return false;
    }

    public static void sendShardlessMessage(String channel, Message msg) {
        sendShardlessMessage(msg.getJDA(), channel, msg.getRawContent());
    }

    public static void sendShardlessMessage(JDA jda, String channel, String content) {
        JSONObject body = new JSONObject();
        body.put("content", content);
        ((JDAImpl) jda).getRequester().post(Requester.DISCORD_API_PREFIX + "channels/" + channel + "/messages", body);
    }

}
