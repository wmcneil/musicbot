package fredboat.audio.queue;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class IdentifierContext {
    
    public final String identifier;
    public final TextChannel textChannel;
    public final User user;

    public IdentifierContext(String identifier, TextChannel textChannel, User user) {
        this.identifier = identifier;
        this.textChannel = textChannel;
        this.user = user;
    }
    
}
