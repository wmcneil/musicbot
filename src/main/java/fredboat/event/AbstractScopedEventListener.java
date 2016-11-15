package fredboat.event;

import fredboat.FredBoat;
import fredboat.util.TextUtils;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.regex.Pattern;

public abstract class AbstractScopedEventListener extends ListenerAdapter {

    public final int scope;
    public final String defaultPrefix;
    public final Pattern commandNamePrefix;
    private final HashMap<String, UserListener> userListener = new HashMap<>();

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

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        UserListener listener = userListener.get(event.getAuthor().getId());
        if (listener != null) {
            try{
            listener.onGuildMessageReceived(event);
            } catch(Exception ex){
                TextUtils.handleException(ex, event.getChannel(), event.getAuthor());
            }
        }
    }

    public void putListener(String id, UserListener listener) {
        userListener.put(id, listener);
    }

    public void removeListener(String id) {
        userListener.remove(id);
    }
}
