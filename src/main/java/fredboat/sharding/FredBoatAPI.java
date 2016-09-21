package fredboat.sharding;

import org.springframework.boot.SpringApplication;

public class FredBoatAPI {
    
    protected static String token = null;
    
    public static void start(String tkn, String[] args) throws Exception {
        token = tkn;
        SpringApplication.run(FredBoatAPI.class, args);
    }
    
}
