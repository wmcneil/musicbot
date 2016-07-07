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

    public CarbonAgent(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    this.wait(1000 * 60);//Only send statistics each minute

                    submitData(FredBoat.IS_BETA ? "carbon.fredboat.commandsExecuted.beta" : "carbon.fredboat.commandsExecuted.production", String.valueOf(CommandManager.commandsExecuted - commandsExecutedLastSubmission));
                    if (!FredBoat.IS_BETA) {
                        submitData("carbon.fredboat.users", String.valueOf(jda.getUsers().size()));
                        submitData("carbon.fredboat.guilds", String.valueOf(jda.getGuilds().size()));
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
