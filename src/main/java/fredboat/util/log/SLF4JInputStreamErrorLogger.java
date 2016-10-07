package fredboat.util.log;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;

public class SLF4JInputStreamErrorLogger extends SLF4JInputStreamLogger {
    
    public SLF4JInputStreamErrorLogger(Logger log, InputStream is) {
        super(log, is);
    }

    @Override
    public void run() {
        try {
            while (true) {
                log.error(br.readLine());
            }
        } catch (IOException ignored) {
            //The stream has ended
        }
    }
    
}
