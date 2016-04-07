package fredboat.mafia.role;

import fredboat.mafia.MafiaGame;
import fredboat.mafia.MafiaPlayer;
import net.dv8tion.jda.utils.InviteUtil;

public class RoleMafioso extends Role {

    public RoleMafioso(MafiaGame game, MafiaPlayer plr) {
        super(game, plr);
    }

    @Override
    public void sendRolePM(MafiaPlayer player, MafiaGame game) {
        String msg = "You are a _**Vanilla Mafioso**_!\n"
        + "Your task is to eliminate the town. You must lynch or murder enough townies such that there are no more townies and there are scum.\n"
                +"Every night you may vote to kill someone in the mafia chat.\n\n"+
                "**Mafia Chat:** "+InviteUtil.createInvite(game.mafiaChannel, InviteUtil.InviteDuration.INFINITE, 50, false, false).getUrl()+
                "\nGood luck!";
        player.getJDA().getUserById(player.getId()).getPrivateChannel().sendMessage(msg);
    }

    @Override
    public Alignment getAlignment() {
        return Alignment.TOWNIES;
    }
    
}
