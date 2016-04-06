package fredboat.mafia.role;

import fredboat.mafia.MafiaGame;
import fredboat.mafia.MafiaPlayer;

public abstract class Role {

    public MafiaGame game;
    public MafiaPlayer plr;

    public Role(MafiaGame game, MafiaPlayer plr) {
        this.game = game;
        this.plr = plr;
    }
    
    public abstract void sendRolePM(MafiaPlayer player, MafiaGame game);
    
    public Alignment getAlignment(){
        return Alignment.MISC;
    }
    
}
