package fredboat.mafia;

import fredboat.mafia.role.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.impl.JDAImpl;
import net.dv8tion.jda.entities.impl.UserImpl;

public class MafiaPlayer extends UserImpl {
    
    public MafiaPlayerStatus status = MafiaPlayerStatus.ALIVE;
    public Role gameRole;

    public MafiaPlayer(String id, JDAImpl api) {
        super(id, api);
        User realUser = api.getUserById(id);
        setUserName(realUser.getUsername());
    }
    
    public void setRole(Role role) {
        gameRole = role;
    }
    
}
