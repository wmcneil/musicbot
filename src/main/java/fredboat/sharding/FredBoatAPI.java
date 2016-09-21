package fredboat.sharding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import org.json.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class FredBoatAPI {
    
    protected static String token = null;
    protected static JDA jda = null;
    
    @RequestMapping("/guilds")
    @ResponseBody
    protected String guilds(HttpServletRequest request, HttpServletResponse response) {
        if(isAuthenticated(request)){
            JSONArray a = new JSONArray();
            
            for(Guild guild : jda.getGuilds()){
                a.put(guild.getId());
            }
            
            return a.toString();
        } else {
            response.setStatus(403);
            return null;
        }
    }
    
    public static boolean isAuthenticated(HttpServletRequest request){
        return request.getHeader("authorization").equals(token);
    }
    
    public static void start(JDA api, String tkn, String[] args) throws Exception {
        jda = api;
        token = tkn;
        SpringApplication.run(FredBoatAPI.class, args);
    }
    
}
