package fredboat.event;

import fredboat.FredBoat;
import fredboat.util.DiscordUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class EventLogger extends ListenerAdapter {

    public final String logChannelId;
    public JDA jda;

    public EventLogger(String logChannelId) {
        this.logChannelId = logChannelId;
    }

    private void send(Message msg) {
        send(msg.getRawContent());
    }

    private void send(String msg) {
        DiscordUtil.sendShardlessMessage(jda, logChannelId,
                "["
                + FredBoat.shardId
                + ":"
                + FredBoat.numShards
                + "] "
                + msg
        );
    }

    @Override
    public void onReady(ReadyEvent event) {
        jda = event.getJDA();
        send(new MessageBuilder()
                .appendString("[:rocket:] Received ready event.")
                .build()
        );
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        send(
                "[:white_check_mark:] Joined guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getUsers().size() + "`."
        );
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        send(
                "[:x:] Left guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getUsers().size() + "`."
        );
    }

    public void onExit(int code) {
        send(
                "[:zzz:] Exiting with code `" + code + "`."
        );
    }

}
