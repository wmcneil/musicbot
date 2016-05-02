package fredboat.command.fun;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.commandmeta.Command;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.impl.MessageImpl;

public class RemoteFileCommand extends Command {

    public String msg;
    public File tmpFile;

    public RemoteFileCommand(String msg) {
        this.msg = msg;
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        InputStream is;
        FileOutputStream fos;
        try {
            if (tmpFile == null || tmpFile.exists() == false || tmpFile.length() == 0L) {
                tmpFile = File.createTempFile(UUID.randomUUID().toString(), ".png");
                is = Unirest.get(msg).asBinary().getRawBody();
                RenderedImage img = ImageIO.read(is);
                ImageIO.write(img, "png", tmpFile);
                tmpFile.deleteOnExit();
            }
        } catch (IOException ex) {
            tmpFile.delete();
            throw new RuntimeException(ex);
        } catch (UnirestException ex) {
            tmpFile.delete();
            throw new RuntimeException(ex);
        }

        channel.sendFile(tmpFile, null);
    }

}
