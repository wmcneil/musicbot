package fredboat.util.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;

public class SLF4JInputStreamLogger extends Thread {

    final Logger log;
    final BufferedReader br;

    public SLF4JInputStreamLogger(Logger log, InputStream is) {
        this.log = log;
        this.br = new BufferedReader(new InputStreamReader(is));
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                log.info(br.readLine());
            }
        } catch (IOException ignored) {
            //The stream has ended
        }
    }

}
