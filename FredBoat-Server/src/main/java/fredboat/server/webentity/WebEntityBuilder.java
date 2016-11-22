package fredboat.server.webentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class WebEntityBuilder {
    
    public static WebUser buildUser(JSONObject json){
        String id = json.getString("id");
        String username = json.getString("username");
        String discriminator = json.getString("discriminator");
        String avatar = json.getString("avatar");
        boolean bot = json.getBoolean("bot");
        return new WebUser(id, username, discriminator, avatar, bot);
    }
    
    public static WebRole buildRole(JSONObject json){
        String id = json.getString("id");
        String name = json.getString("name");
        int color = json.getInt("color");
        int position = json.getInt("position");
        int permissions = json.getInt("permissions");
        return new WebRole(id, name, color, position, permissions);
    }
    
    public static List<WebRole> buildRoles(JSONArray a){
        ArrayList<WebRole> list = new ArrayList<>();
        Iterator<Object> itr = a.iterator();
        while(itr.hasNext()){
            list.add(buildRole((JSONObject) itr.next()));
        }
        
        return list;
    }
    
}
