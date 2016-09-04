package fredboat.event;

import fredboat.FredBoat;
import java.util.regex.Pattern;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public abstract class AbstractScopedEventListener extends ListenerAdapter {
    public final int scope;
    public final String defaultPrefix;
    public final Pattern commandNamePrefix;
    
    public static int messagesReceived = 0;

    public AbstractScopedEventListener(int scope, String prefix) {
        this.scope = scope;
        this.defaultPrefix = prefix;
        this.commandNamePrefix = Pattern.compile("(\\w+)");
    }

    @Override
    public void onReady(ReadyEvent event) {
        FredBoat.init(event);
    }
    
}
