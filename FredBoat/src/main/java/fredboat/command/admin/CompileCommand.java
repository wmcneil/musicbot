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

package fredboat.command.admin;

import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.ICommandOwnerRestricted;
import fredboat.util.log.SLF4JInputStreamErrorLogger;
import fredboat.util.log.SLF4JInputStreamLogger;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CompileCommand extends Command implements ICommandOwnerRestricted {

    private static final Logger log = LoggerFactory.getLogger(CompileCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        try {
            Runtime rt = Runtime.getRuntime();
            Message msg = channel.sendMessage("*Now updating...*\n\nRunning `git clone`... ");
            String branch = "master";
            if (args.length > 1) {
                branch = args[1];
            }

            //Clear any old update folder if it is still present
            try {
                Process rm = rt.exec("rm -rf update");
                rm.waitFor(5, TimeUnit.SECONDS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Process gitClone = rt.exec("git clone https://github.com/Frederikam/FredBoat.git --branch " + branch + " --single-branch update");
            new SLF4JInputStreamLogger(log, gitClone.getInputStream()).start();
            new SLF4JInputStreamErrorLogger(log, gitClone.getInputStream()).start();

            if (!gitClone.waitFor(120, TimeUnit.SECONDS)) {
                msg = msg.updateMessage(msg.getRawContent() + "[:anger: timed out]\n\n");
                throw new RuntimeException("Operation timed out: git clone");
            } else if (gitClone.exitValue() != 0) {
                msg = msg.updateMessage(msg.getRawContent() + "[:anger: returned code " + gitClone.exitValue() + "]\n\n");
                throw new RuntimeException("Bad response code");
            }

            msg = msg.updateMessage(msg.getRawContent() + "👌🏽\n\nRunning `mvn package shade:shade`... ");
            File updateDir = new File("./FredBoat/update");

            Process mvnBuild = rt.exec("mvn -f " + updateDir.getAbsolutePath() + "/pom.xml package shade:shade");
            new SLF4JInputStreamLogger(log, mvnBuild.getInputStream()).start();
            new SLF4JInputStreamErrorLogger(log, mvnBuild.getInputStream()).start();

            if (!mvnBuild.waitFor(600, TimeUnit.SECONDS)) {
                msg = msg.updateMessage(msg.getRawContent() + "[:anger: timed out]\n\n");
                throw new RuntimeException("Operation timed out: mvn package shade:shade");
            } else if (mvnBuild.exitValue() != 0) {
                msg = msg.updateMessage(msg.getRawContent() + "[:anger: returned code " + mvnBuild.exitValue() + "]\n\n");
                throw new RuntimeException("Bad response code");
            }

            msg.updateMessage(msg.getRawContent() + "👌🏽");

            if(!new File("./update/target/FredBoat/FredBoat-1.0.jar").renameTo(new File(System.getProperty("user.home") + "/FredBoat-1.0.jar"))){
                throw new RuntimeException("Failed to mmove jar to home");
            }
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
