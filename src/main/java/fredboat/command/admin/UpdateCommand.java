package fredboat.command.admin;

import fredboat.FredBoat;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.ICommandOwnerRestricted;
import fredboat.util.ExitCodes;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class UpdateCommand extends Command implements ICommandOwnerRestricted {

    private static final Logger log = LoggerFactory.getLogger(UpdateCommand.class);
    private static final CompileCommand COMPILE_COMMAND = new CompileCommand();
    private static final long MAX_JAR_AGE = 10 * 60 * 1000;

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        try {
            File homeJar = new File(System.getProperty("user.home") + "/FredBoat-1.0.jar");

            //Must exist and not be too old
            if(homeJar.exists()
                    && (System.currentTimeMillis() - homeJar.lastModified()) < MAX_JAR_AGE){
                update(channel);
                return;
            } else {
                log.info("");
            }

            COMPILE_COMMAND.onInvoke(guild, channel, invoker, message, args);

            update(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(TextChannel channel) throws IOException {
        File homeJar = new File(System.getProperty("user.home") + "/FredBoat-1.0.jar");
        File targetJar = new File("./update/target/FredBoat-1.0.jar");

        targetJar.getParentFile().mkdirs();
        targetJar.delete();
        FileUtils.copyFile(homeJar, targetJar);

        //Shutdown for update
        channel.sendMessage("Now restarting...");
        FredBoat.shutdown(ExitCodes.EXIT_CODE_UPDATE);
    }

}
