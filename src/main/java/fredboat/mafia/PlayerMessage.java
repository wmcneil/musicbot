package fredboat.mafia;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;

public class PlayerMessage {
    
    private final Message msg;
    private final MafiaPlayer player;
    private final MessageChannel channel;
    private final Guild guild;

    public PlayerMessage(Message msg, MafiaPlayer player, MessageChannel channel, Guild guild) {
        this.msg = msg;
        this.player = player;
        this.channel = channel;
        this.guild = guild;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public Message getMsg() {
        return msg;
    }

    public MafiaPlayer getPlayer() {
        return player;
    }
    
    public Guild getGuild() {
        return guild;
    }
    
}