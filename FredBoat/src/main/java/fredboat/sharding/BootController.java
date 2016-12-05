/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Frederik Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fredboat.sharding;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableAutoConfiguration
@Controller
@ComponentScan
public class BootController {

    private final String token;
    private final JDA jda;

    public BootController() {
        this.token = FredBoatAPIServer.token;
        this.jda = FredBoatAPIServer.jda;
    }

    @RequestMapping("/guildCount")
    @ResponseBody
    private String guildCount(HttpServletRequest request, HttpServletResponse response) {
        if (isAuthenticated(request)) {
            return String.valueOf(jda.getGuilds().size());
        } else {
            response.setStatus(403);
            return null;
        }
    }
    
    @RequestMapping("/globalGuildCount")
    @ResponseBody
    private String globalGuildCount(HttpServletRequest request, HttpServletResponse response) {
        if (isAuthenticated(request)) {
            return String.valueOf(ShardTracker.getGlobalGuildCount());
        } else {
            response.setStatus(403);
            return null;
        }
    }

    @RequestMapping("/users")
    @ResponseBody
    private String users(HttpServletRequest request, HttpServletResponse response) {
        if (isAuthenticated(request)) {
            JSONArray a = new JSONArray();

            for (User user : FredBoatAPIServer.jda.getUsers()) {
                a.put(user.getId());
            }

            return a.toString();
        } else {
            response.setStatus(403);
            return null;
        }
    }
    
    @RequestMapping("/globalUserCount")
    @ResponseBody
    private String globalUserCount(HttpServletRequest request, HttpServletResponse response) {
        if (isAuthenticated(request)) {
            return String.valueOf(ShardTracker.getGlobalUserCount());
        } else {
            response.setStatus(403);
            return null;
        }
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        return request.getHeader("authorization").equals(token);
    }

}
