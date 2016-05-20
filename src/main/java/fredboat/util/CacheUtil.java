package fredboat.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class CacheUtil {

    private static HashMap<String, File> cachedURLFiles = new HashMap<>();

    public static File getImageFromURL(String url) {
        if (cachedURLFiles.containsKey(url) && cachedURLFiles.get(url).exists()) {
            //Already cached
            return cachedURLFiles.get(url);
        } else {
            InputStream is;
            FileOutputStream fos;
            File tmpFile = null;
            try {
                //Use regex to find the file extension
                Matcher matcher = Pattern.compile("\\.(\\w+$)").matcher(url);
                matcher.find();
                String type = matcher.group();

                tmpFile = File.createTempFile(UUID.randomUUID().toString(), "." + type);
                is = Unirest.get(url).asBinary().getRawBody();
                RenderedImage img = ImageIO.read(is);
                ImageIO.write(img, type, tmpFile);
                tmpFile.deleteOnExit();
            } catch (IOException ex) {
                tmpFile.delete();
                throw new RuntimeException(ex);
            } catch (UnirestException ex) {
                tmpFile.delete();
                throw new RuntimeException(ex);
            }
        }

        return null;
    }

}
