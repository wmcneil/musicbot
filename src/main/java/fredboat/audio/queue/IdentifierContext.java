package fredboat.audio.queue;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class IdentifierContext {

    public final String identifier;
    public final TextChannel textChannel;
    public final User user;
    private boolean quiet = false;
    private long position = 0L;

    public IdentifierContext(String identifier, TextChannel textChannel) {
        this.identifier = identifier;
        this.textChannel = textChannel;
        this.user = null;
    }

    public IdentifierContext(String identifier, TextChannel textChannel, User user) {
        this.identifier = identifier;
        this.textChannel = textChannel;
        this.user = user;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

}
