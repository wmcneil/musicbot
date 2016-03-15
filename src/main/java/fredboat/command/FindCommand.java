package fredboat.command;

import fredboat.command.meta.ICommand;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.MessageHistory;

public class FindCommand implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        JDA jda = guild.getJDA();

        String searchTerm = args[1].toLowerCase();
        TextChannel selected = channel;
        if (message.getMentionedChannels().isEmpty() == false) {
            selected = message.getMentionedChannels().get(0);
        }
        int toSearch = 1337;
        channel.sendTyping();

        Message startMsg = new MessageBuilder().appendMention(invoker)
                .appendString(" searching in ")
                .appendString(String.valueOf(toSearch), MessageBuilder.Formatting.BLOCK)
                .appendString(" messages from ")
                .appendString(selected.getName(), MessageBuilder.Formatting.BLOCK)
                .appendString(" containing ")
                .appendString(searchTerm, MessageBuilder.Formatting.BLOCK)
                .appendString(".")
                .build();

        MessageHistory history = new MessageHistory(jda, selected);
        ArrayList<Message> msgs = new ArrayList<>();
        
        try {
            for (int i = 0; i < Math.ceil(toSearch / 100); i++) {
                msgs.addAll(history.retrieve(Math.min(100, toSearch - (i * 100))));
            }
        } catch (NullPointerException ex) {//End of chat - ignore
        }
        
        channel.sendMessage(startMsg);
        ArrayList<Message> matches = new ArrayList<>();

        for (Message msg : msgs) {
            if (msg.getContent().toLowerCase().contains(searchTerm) && !msg.equals(message)) {
                matches.add(msg);
            }
        }

        MessageBuilder endMsg = new MessageBuilder();
        endMsg.appendString("Found a total of ")
                .appendString(String.valueOf(matches.size()), MessageBuilder.Formatting.BLOCK)
                .appendString(" matches:");

        int i = 0;
        int truncated = 0;
        for (Message msg : matches) {
            i++;
            if (endMsg.getLength() > 1000 || msg.getContent().length() > 500) {
                truncated++;
            } else {
                endMsg.appendString("\n")
                        .appendString("["+i+"] " + msg.getTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), MessageBuilder.Formatting.BLOCK)
                        .appendString(" ")
                        .appendString(msg.getAuthor().getUsername(), MessageBuilder.Formatting.BLOCK)
                        .appendString(" ")
                        .appendString(msg.getContent());
            }
        }
        
        if (truncated > 0){
            endMsg.appendString("\n[Truncated "+truncated+"]");
        }
        channel.sendMessage(endMsg.build());

    }

}
