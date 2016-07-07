package fredboat.agent;

import fredboat.FredBoat;
import fredboat.commandmeta.CommandManager;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.JDA;

public class CarbonAgent extends Thread {

    public static final String CARBON_HOST = "192.210.193.136";
    public static final int CARBON_PORT = 2003;
    private int commandsExecutedLastSubmission = 0;
    public final JDA jda;
    public final String buildStream;

    public CarbonAgent(JDA jda) {
        this.jda = jda;
        buildStream = FredBoat.IS_BETA ? "beta" : "production";
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    this.wait(1000 * 300);//Only send statistics every 5 minutes

                    submitData("carbon.fredboat.commandsExecuted." + buildStream, String.valueOf(CommandManager.commandsExecuted - commandsExecutedLastSubmission));
                    submitData("carbon.fredboat.users." + buildStream, String.valueOf(jda.getUsers().size()));
                    submitData("carbon.fredboat.guilds." + buildStream, String.valueOf(jda.getGuilds().size()));
                    if(!FredBoat.IS_BETA){
                        submitData("carbon.fredboat.memoryUsage." + buildStream, String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));//In bytes
                    }
                    commandsExecutedLastSubmission = CommandManager.commandsExecuted;
                    //Track command usage
                } catch (InterruptedException ex) {
                    Logger.getLogger(CarbonAgent.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
            }

        }
    }

    public static void submitData(String path, String value) {
        try {
            String output = path + " " + value + " " + System.currentTimeMillis() / 1000;
            Socket socket = new Socket(CARBON_HOST, CARBON_PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeBytes(output + "\n");
            dos.flush();
            socket.close();

            System.out.println("Submitted data: " + output);
        } catch (IOException ex) {
            Logger.getLogger(CarbonAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
