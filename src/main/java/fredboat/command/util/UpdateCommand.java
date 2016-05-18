package fredboat.command.util;

import fredboat.event.EventListenerBoat;
import fredboat.commandmeta.Command;
import fredboat.commandmeta.ICommandOwnerRestricted;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class UpdateCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        try {
            Message msg = channel.sendMessage("*Now updating...*\n\nRunning `git clone`... ");
            String branch = "master";
            if (args.length > 1) {
                branch = args[1];
            }

            Runtime rt = Runtime.getRuntime();
            Process gitClone = rt.exec("git clone https://github.com/Frederikam/FredBoat.git --branch " + branch + " --single-branch update");
            if (gitClone.waitFor(120, TimeUnit.SECONDS) == false) {
                msg.updateMessage(msg.getRawContent() + "[:anger: timed out]\n\n");
                throw new RuntimeException("Operation timed out: git clone");
            } else if (gitClone.exitValue() != 0) {
                msg.updateMessage(msg.getRawContent() + "[:anger: returned code " + gitClone.exitValue() + "]\n\n");
                throw new RuntimeException("Bad response code");
            }

            msg.updateMessage(msg.getRawContent() + "👌🏽\n\nRunning `mv package shade:shade`... ");
            File updateDir = new File("./update");
            Process mvnBuild = rt.exec("mvn -f " + updateDir.getAbsolutePath() + "/pom.xml package shade:shade");
            if (mvnBuild.waitFor(300, TimeUnit.SECONDS) == false) {
                msg.updateMessage(msg.getRawContent() + "[:anger: timed out]\n\n");
                throw new RuntimeException("Operation timed out: mvn package shade:shade");
            } else if (mvnBuild.exitValue() != 0) {
                msg.updateMessage(msg.getRawContent() + "[:anger: returned code " + mvnBuild.exitValue() + "]\n\n");
                throw new RuntimeException("Bad response code");
            }
            msg.updateMessage(msg.getRawContent() + "👌🏽\n\nNow restarting...");

            ClassLoader classLoader = getClass().getClassLoader();
            File script = new File(classLoader.getResource("updater.sh").getFile());
            Scanner ss = new Scanner(script);
            String scriptSource = ss.next(".*");
            rt.exec(scriptSource);
            channel.getJDA().shutdown(true);
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
