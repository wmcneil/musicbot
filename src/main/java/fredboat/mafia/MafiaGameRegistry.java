package fredboat.mafia;

import java.util.concurrent.CopyOnWriteArrayList;
import net.dv8tion.jda.entities.User;

public class MafiaGameRegistry {
    
    public static CopyOnWriteArrayList<MafiaGame> activeGames = new CopyOnWriteArrayList<>();
    
    public static void add(MafiaGame game){
        activeGames.add(game);
    }
    
    public static boolean remove(MafiaGame game){
        return activeGames.remove(game);
    }
    
    public static boolean isPlayerAlreadyInGame(User usr){
        for (MafiaGame game : activeGames){
            for (MafiaPlayer p : game.players){
                if(p.getId().equals(usr.getId())){
                    return true;
                }
            }
        }
        
        return false;
    }
    
    
    
}
