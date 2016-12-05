/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Frederik Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package fredboat.event;

import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.command.fun.TalkCommand;
import fredboat.commandmeta.CommandManager;
import fredboat.commandmeta.CommandRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.util.BotConstants;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.voice.VoiceLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.regex.Matcher;

import static fredboat.FredBoat.jdaBot;

public class EventListenerBoat extends AbstractScopedEventListener {

    private static final Logger log = LoggerFactory.getLogger(EventListenerBoat.class);

    public static HashMap<String, Message> messagesToDeleteIfIdDeleted = new HashMap<>();
    public User lastUserToReceiveHelp;

    public static int messagesReceived = 0;

    public EventListenerBoat(int scope, String defaultPrefix) {
        super(scope, defaultPrefix);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        /*log.info(event.getJDA().getSelfInfo().getUsername());
        log.info(event);
        log.info(event.getAuthor());
        log.info(event.getAuthor().getId());*/

        messagesReceived++;

        if (event.getPrivateChannel() != null) {
            log.info("PRIVATE" + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
            return;
        }

        if (event.getAuthor().getUsername().equals(event.getJDA().getSelfInfo().getUsername())) {
            log.info(event.getGuild().getName() + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
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
        } else if (event.getMessage().getRawContent().startsWith("<@" + jdaBot.getSelfInfo().getId() + ">")) {
            log.info(event.getGuild().getName() + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
            CommandManager.commandsExecuted++;
            TalkCommand.talk(event.getAuthor(), event.getTextChannel(), event.getMessage().getRawContent().substring(jdaBot.getSelfInfo().getAsMention().length() + 1));
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (messagesToDeleteIfIdDeleted.containsKey(event.getMessageId())) {
            Message msg = messagesToDeleteIfIdDeleted.remove(event.getMessageId());
            if (msg.getJDA() == jdaBot) {
                msg.deleteMessage();
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        //Ignore self
        if (event.getAuthor().getUsername().equals(event.getJDA().getSelfInfo().getUsername())) {
            return;
        }

        if (event.getAuthor() == lastUserToReceiveHelp) {
            //Ignore, they just got help! Stops any bot chain reactions
            return;
        }

        event.getChannel().sendMessage(BotConstants.HELP_TEXT);
        lastUserToReceiveHelp = event.getAuthor();
    }

    @Override
    public void onReady(ReadyEvent event) {
        super.onReady(event);
        jdaBot.getAccountManager().setGame("Say ;;help");
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        jdaBot.getAccountManager().setGame("Say ;;help");
    }

    /* music related */
    @Override
    public void onVoiceLeave(VoiceLeaveEvent event) {
        GuildPlayer player = PlayerRegistry.getExisting(event.getGuild());

        if (player == null) {
            return;
        }

        if (player.getUsersInVC().isEmpty()
                && player.getUserCurrentVoiceChannel(jdaBot.getSelfInfo()) == event.getOldChannel()
                && player.isPaused() == false) {
            player.pause();
            player.getActiveTextChannel().sendMessage("All users have left the voice channel. The player has been paused.");
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        PlayerRegistry.destroyPlayer(event.getGuild());
    }

}
