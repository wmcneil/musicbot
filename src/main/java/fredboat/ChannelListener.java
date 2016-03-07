/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fredboat;

import static fredboat.FredBoat.jda;
import fredboat.command.meta.CommandManager;
import java.util.ArrayList;
import java.util.HashMap;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.InviteReceivedEvent;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.ReconnectedEvent;
import net.dv8tion.jda.events.message.MessageDeleteEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.InviteUtil;

public class ChannelListener extends ListenerAdapter {

    public static HashMap<String, Message> messagesToDeleteIfIdDeleted = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getPrivateChannel() != null) {
            System.out.println("PRIVATE" + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
            return;
        }

        if (event.getAuthor().getUsername().equals(event.getJDA().getSelfInfo().getUsername())) {
            System.out.println(event.getGuild().getName() + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
            return;
        }

        if (event.getMessage().getContent().length() < FredBoat.PREFIX.length()) {
            return;
        }

        if (event.getMessage().getContent().endsWith("(╯°□°）╯︵ ┻━┻")) {
            tableflip(event);
            return;
        }

        if (event.getMessage().getContent().substring(0, fredboat.FredBoat.PREFIX.length()).equals(fredboat.FredBoat.PREFIX)) {
            System.out.println(event.getGuild().getName() + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
            CommandManager.prefixCalled(event.getGuild(), event.getTextChannel(), event.getAuthor(), event.getMessage());
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (messagesToDeleteIfIdDeleted.containsKey(event.getMessageId())) {
            messagesToDeleteIfIdDeleted.get(event.getMessageId()).deleteMessage();
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        //Ignore self
        if (event.getAuthor().getUsername().equals(event.getJDA().getSelfInfo().getUsername())) {
            return;
        }

        //Ignore invites (handled elsewhere)
        if (event.getMessage().getContent().contains("discord.gg")) {
            return;
        }

        event.getChannel().sendMessage(FredBoat.helpMsg);
    }

    @Override
    public void onInviteReceived(InviteReceivedEvent event) {
        if (event.getMessage().isPrivate()) {
            //System.out.println(event.getInvite().getUrl());
            //InviteUtil.join(event.getInvite(), FredBoat.jda);
            Guild guild = null;
            try {
                guild = FredBoat.jda.getGuildById(event.getInvite().getGuildId());
            } catch (NullPointerException ex) {
                event.getAuthor().getPrivateChannel().sendMessage("That invite is not valid!");
                return;
            }

            boolean isNotInGuild = true;

            if (isNotInGuild) {
                event.getAuthor().getPrivateChannel().sendMessage("Invite accepted!");
                InviteUtil.join(event.getInvite(), FredBoat.jda);
            } else {
                event.getAuthor().getPrivateChannel().sendMessage("Already in that channel!");
            }
        }
    }

    public HashMap<String, ArrayList<Integer>> recentTableFlips = new HashMap<>();
    
    public void pruneRecentTableflips(Guild guild, int seconds){
        ArrayList<Integer> recent = recentTableFlips.containsKey(guild.getId()) ? recentTableFlips.get(guild.getId()) : new ArrayList<>();
        for(int time : recent){
            
        }
    }
    
    public void getRecentTableflips(Guild guild, int seconds){
        
    }
    
    public void tableflip(MessageReceivedEvent event) {
        //System.out.println(event.getGuild().getName() + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
        //event.getChannel().sendMessage("┬─┬﻿ ノ( ゜-゜ノ)");
    }

    @Override
    public void onReady(ReadyEvent event) {
        FredBoat.init();
        jda.getAccountManager().setGame("Say ;;help");
    }
    
    @Override
    public void onReconnect(ReconnectedEvent event) {
        jda.getAccountManager().setGame("Say ;;help");
    }

}
