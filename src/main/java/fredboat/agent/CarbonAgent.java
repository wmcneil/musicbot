package fredboat.agent;

import fredboat.FredBoat;
import fredboat.commandmeta.CommandManager;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarbonAgent extends Thread {

    public static final String CARBON_HOST = "192.210.193.136";
    public static final int CARBON_PORT = 2003;
    private int commandsExecutedLastSubmission = 0;

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    this.wait(1000 * 60);//Only send statistics each minute

                    submitData(FredBoat.IS_BETA ? "carbon.fredboat.commandsExecuted.beta" : "carbon.fredboat.commandsExecuted.production", String.valueOf(CommandManager.commandsExecuted - commandsExecutedLastSubmission));
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
            /*WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5);
            WebSocket socket = factory.createSocket(CARBON_URI);
            socket.connect();
            socket.sendText(path + " " + value + " " + System.currentTimeMillis());
            socket.disconnect();*/
            String output = path + " " + value + " " + System.currentTimeMillis()/1000;
            Socket socket = new Socket(CARBON_HOST, CARBON_PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeBytes(output);
            dos.flush();
            socket.close();
            
            System.out.println("Submitted data: " + output);
        } catch (IOException ex) {
            Logger.getLogger(CarbonAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
