package fredboat.util;

import fredboat.FredBoat;
import java.util.List;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;

public class DiscordUtil {

    public static boolean isMainBot(){
        return (FredBoat.scopes & 0x100) != 0;
    }
    
    public static boolean isMusicBot(){
        return (FredBoat.scopes & 0x010) != 0;
    }
    
    public static boolean isSelfBot(){
        return (FredBoat.scopes & 0x001) != 0;
    }
    
    public static boolean isMainBotPresent(Guild guild) {
        JDA jda = guild.getJDA();
        User other = jda.getUserById(FredBoat.MAIN_BOT_ID);
        return guild.getUsers().contains(other);
    }
    
    public static boolean isMusicBotPresent(Guild guild) {
        JDA jda = guild.getJDA();
        User other = jda.getUserById(FredBoat.MUSIC_BOT_ID);
        return guild.getUsers().contains(other);
    }
    
    public static boolean isUserBotCommander(Guild guild, User user){
        List<Role> roles = guild.getRolesForUser(user);
        
        for(Role r : roles){
            if(r.getName().equals("Bot Commander")){
                return true;
            }
        }
        
        return false;
    }

}
