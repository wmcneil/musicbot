package fredboat.util;

public class BrainfuckException extends RuntimeException {

    private BrainfuckException() {
    }
    
    public BrainfuckException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public BrainfuckException(String string) {
        super(string);
    }

}
