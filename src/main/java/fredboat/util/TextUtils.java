package fredboat.util;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class TextUtils {

    public static Message prefaceWithMention(User user, String msg) {
        MessageBuilder builder = new MessageBuilder().appendMention(user).appendString(msg);
        return builder.build();
    }
    
    public static Message replyWithMention(TextChannel channel, User user, String msg) {
        MessageBuilder builder = new MessageBuilder().appendMention(user).appendString(msg);
        Message mes = builder.build();
        channel.sendMessage(mes);
        return mes;
    }

}
