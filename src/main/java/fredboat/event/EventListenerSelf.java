/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fredboat.event;

import fredboat.commandmeta.CommandManager;
import fredboat.commandmeta.CommandRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.util.BotConstants;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;

public class EventListenerSelf extends AbstractScopedEventListener {

    private static final Logger log = LoggerFactory.getLogger(EventListenerSelf.class);
    
    //public static HashMap<String, Message> messagesToDeleteIfIdDeleted = new HashMap<>();
    //public static HashMap<VoiceChannel, Runnable> toRunOnConnectingToVoice = new HashMap<>();
    public User lastUserToReceiveHelp;

    public EventListenerSelf(int scope, String defaultPrefix) {
        super(scope, defaultPrefix);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(BotConstants.OWNER_ID)) {
            return;
        }

        if (event.getMessage().getContent().length() < defaultPrefix.length()) {
            return;
        }

        if (event.getMessage().getContent().substring(0, defaultPrefix.length()).equals(defaultPrefix)) {
            Command invoked = null;
            try {
                log.info(event.getGuild().getName() + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
                Matcher matcher = commandNamePrefix.matcher(event.getMessage().getContent());
                matcher.find();

                invoked = CommandRegistry.getCommandFromScope(scope, matcher.group()).command;
            } catch (NullPointerException ex) {

            }

            if (invoked == null) {
                return;
            }

            CommandManager.prefixCalled(invoked, event.getGuild(), event.getTextChannel(), event.getAuthor(), event.getMessage());

            try {
                event.getMessage().deleteMessage();
            } catch (Exception ex) {
            }

        }
    }

}
