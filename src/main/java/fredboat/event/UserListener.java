package fredboat.event;

import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public abstract class UserListener {
    
    public abstract void onGuildMessageReceived(GuildMessageReceivedEvent event);
    
}
