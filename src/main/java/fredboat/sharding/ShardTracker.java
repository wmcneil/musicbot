package fredboat.sharding;

import net.dv8tion.jda.JDA;

public class ShardTracker extends Thread {
    
    public final JDA jda;
    public static ShardTracker ins = null;
    
    private static int globalUserCount = 0;
    private static int globalGuildCount = 0;
    
    public ShardTracker(JDA jda, String token) {
        if(ins != null){
            throw new IllegalStateException("Only one instance may exist.");
        }
        
        this.jda = jda;
        
        setDaemon(true);
        setName(this.getClass().getSimpleName());
        ins = this;
    }

    public static int getGlobalUserCount(){
        return globalUserCount;
    }
    
    public static int getGlobalGuildCount(){
        return globalGuildCount;
    }
    
    @Override
    public void run() {
        
    }
    
}
