package fredboat.mafia.role;

import fredboat.mafia.MafiaGame;
import fredboat.mafia.MafiaPlayer;

public class RoleBlue extends Role {

    public RoleBlue(MafiaGame game, MafiaPlayer plr) {
        super(game, plr);
    }

    @Override
    public void sendRolePM(MafiaPlayer player, MafiaGame game) {
        String msg = "You are a _**Vanilla Townie**_!\n"
        + "Your goal is simple. Eliminate all members of the mafia. Your vote and your voice are your only weapons.";
        player.getJDA().getUserById(player.getId()).getPrivateChannel().sendMessage(msg);
    }

    @Override
    public Alignment getAlignment() {
        return Alignment.TOWNIES;
    }
    
}
