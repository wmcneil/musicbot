package fredboat.sharding;

import javax.servlet.http.HttpServletRequest;
import net.dv8tion.jda.JDA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class FredBoatAPI {
    
    protected static String token = null;
    protected static JDA jda = null;
    
    public static boolean isAuthenticated(HttpServletRequest request){
        return request.getHeader("authorization").equals(token);
    }
    
    public static void start(JDA api, String tkn, String[] args) throws Exception {
        jda = api;
        token = tkn;
        SpringApplication.run(BootController.class, args);
    }
    
}
