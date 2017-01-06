package fredboat.db;

import fredboat.commandmeta.MessagingException;

public class DatabaseNotReadyException extends MessagingException {

    DatabaseNotReadyException(String str) {
        super(str);
    }

    DatabaseNotReadyException() {
        super("The database isn't ready yet. The bot might have just started. Please try again in a moment.");
    }
}
