package fredboat.sharding;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import net.dv8tion.jda.JDA;
import org.springframework.boot.SpringApplication;

public class FredBoatAPIServer {

    protected static String token;
    protected static JDA jda;
    public static FredBoatAPIServer ins = null;
    private final String[] args;
    public static final HashMap<String, String> HEADERS = new HashMap<>();

    public FredBoatAPIServer(JDA jda, String token, String[] args) {
        if (ins != null) {
            throw new IllegalStateException("Only one instance may exist.");
        }

        this.jda = jda;
        this.token = token;
        this.args = args;
        HEADERS.put("authorization", token);
        
        ins = this;
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        return request.getHeader("authorization") != null && request.getHeader("authorization").equals(token);
    }

    public void start() {
        SpringApplication.run(BootController.class, args);
    }

}
