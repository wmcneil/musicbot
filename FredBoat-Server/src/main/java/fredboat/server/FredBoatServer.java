package fredboat.server;

import fredboat.common.db.DatabaseManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import org.json.JSONObject;

public class FredBoatServer {
    
    private static JSONObject credsjson;
    private static final boolean IS_WINDOWS_MACHINE = System.getProperty("os.name").toLowerCase().contains("windows");
    
    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) throws FileNotFoundException {
        InputStream is = new FileInputStream(new File("./credentials.json"));
        Scanner scanner = new Scanner(is);
        credsjson = new JSONObject(scanner.useDelimiter("\\A").next());
        scanner.close();
        
        String baseUrl = IS_WINDOWS_MACHINE ? "https://localhost/" : "https://fredboat.frederikam.com/";
        new RestController(getFredBoatToken(), new String[0], baseUrl).start();
        DatabaseManager.startup(credsjson);
        AccountManager.init(getClientId(), getClientSecret());
    }
    
    public static String getFredBoatToken(){
        return credsjson.getString("fredboatToken");
    }
    
    public static String getClientId(){
        return credsjson.getString("clientId");
    }
    
    public static String getClientSecret(){
        return credsjson.getString("secret");
    }
    
}
