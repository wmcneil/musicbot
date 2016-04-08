package fredboat.util.mafia;

import fredboat.mafia.MafiaGame;
import fredboat.mafia.MafiaPlayer;
import fredboat.mafia.MafiaPlayerStatus;
import fredboat.mafia.role.Alignment;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.managers.PermissionOverrideManager;
import net.dv8tion.jda.utils.PermissionUtil;

public class MafiaUtil {

    public static void refreshAllMafiaChatPermissions(MafiaGame game){
        for(MafiaPlayer plr : game.players){
            refreshMafiaChatPermssions(game, plr);
        }
    }
    
    public static void refreshMafiaChatPermssions(MafiaGame game, MafiaPlayer plr) {
        if (game.scumChatGuild.getUsers().contains(plr)) {
            //Kick non-admin townies
            if (plr.gameRole.getAlignment() != Alignment.MAFIA && PermissionUtil.checkPermission(plr, Permission.MANAGE_PERMISSIONS, game.scumChatGuild) == false) {
                game.scumChatGuild.getManager().kick(plr);
            } else if (plr.gameRole.getAlignment() == Alignment.MAFIA) {
                //Grant permission to view the mafia chat
                PermissionOverrideManager pom = game.mafiaChannel.createPermissionOverride(plr)
                        .grant(Permission.MESSAGE_READ)
                        .grant(Permission.MESSAGE_WRITE)
                        .grant(Permission.MESSAGE_HISTORY);
                
                if(plr.status == MafiaPlayerStatus.DEAD){
                    //Dead people may only watch
                    pom.deny(Permission.MESSAGE_WRITE);
                }
                pom.update();
            }
        }
    }

}
