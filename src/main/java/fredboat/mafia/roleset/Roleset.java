package fredboat.mafia.roleset;

import fredboat.mafia.MafiaGame;
import fredboat.mafia.MafiaPlayer;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Roleset {
    
    public abstract int getSize();
    
    public abstract CopyOnWriteArrayList<MafiaPlayer> assignRoles(MafiaGame game, CopyOnWriteArrayList<MafiaPlayer> players);
    
}
