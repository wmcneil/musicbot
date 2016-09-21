package fredboat.sharding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import org.json.JSONArray;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ComponentScan
public class BootController {
    
    private final String token;
    private final JDA jda;

    public BootController() {
        this.token = FredBoatAPI.token;
        this.jda = FredBoatAPI.jda;
    }
    
    @RequestMapping("/guilds")
    @ResponseBody
    private String guilds(HttpServletRequest request, HttpServletResponse response) {
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
    
    @RequestMapping("/users")
    @ResponseBody
    private String users(HttpServletRequest request, HttpServletResponse response) {
        if(isAuthenticated(request)){
            JSONArray a = new JSONArray();
            
            for(User user : FredBoatAPI.jda.getUsers()){
                a.put(user.getId());
            }
            
            return a.toString();
        } else {
            response.setStatus(403);
            return null;
        }
    }
    
    public boolean isAuthenticated(HttpServletRequest request){
        return request.getHeader("authorization").equals(token);
    }
    
}
