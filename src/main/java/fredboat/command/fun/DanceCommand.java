package fredboat.command.fun;

import fredboat.commandmeta.ICommand;
import fredboat.util.HttpUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

public class DanceCommand implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        Runnable func = new Runnable() {
            @Override
            public void run() {
                synchronized (channel) {
                    Message msg = channel.sendMessage("\\o\\");
                    long start = System.currentTimeMillis();
                    try {
                        synchronized (this) {
                            while (start + 20000 > System.currentTimeMillis()) {
                                wait(1000);
                                msg = msg.updateMessage("/o/");
                                wait(1000);
                                msg = msg.updateMessage("\\o\\");
                            }
                        }
                    } catch (InterruptedException interruptedException) {
                    }
                }
            }
        };

        Thread thread = new Thread(func);
        thread.start();
    }
}
