package fredboat.mafia.roleset;

import fredboat.mafia.MafiaGame;
import fredboat.mafia.MafiaPlayer;
import fredboat.mafia.role.RoleBlue;
import fredboat.mafia.role.RoleMafioso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class RolesetClassic extends Roleset {

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public CopyOnWriteArrayList<MafiaPlayer> assignRoles(MafiaGame game, CopyOnWriteArrayList<MafiaPlayer> players) {
        Collections.shuffle(players, new Random(System.nanoTime()));
        Collections.shuffle(players, new Random(System.nanoTime()));
        
        int i = 0;
        players.get(i).setRole(new RoleBlue(game, players.get(i++)));
        //players.get(i++).setRole(new RoleBlue(game, players.get(i)));
        
        players.get(i).setRole(new RoleMafioso(game, players.get(i++)));
        
        Collections.shuffle(players, new Random(System.nanoTime()));
        Collections.shuffle(players, new Random(System.nanoTime()));
        
        return players;
    }
    
}
