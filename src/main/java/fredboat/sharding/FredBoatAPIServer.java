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
public class FredBoatAPIServer {

    protected static String token;
    protected static JDA jda;
    public static FredBoatAPIServer ins = null;
    private final String[] args;

    public FredBoatAPIServer(JDA jda, String token, String[] args) {
        if (ins != null) {
            throw new IllegalStateException("Only one instance may exist.");
        }

        this.jda = jda;
        this.token = token;
        this.args = args;

        ins = this;
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        return request.getHeader("authorization").equals(token);
    }

    public void start() {
        SpringApplication.run(BootController.class, args);
    }

}
