/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fredboat.command.fun;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.FredBoat;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.ICommand;
import fredboat.event.EventListenerBoat;
import fredboat.util.TextUtils;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 * @author frederik
 */
public class LeetCommand extends Command implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        String res = "";
        channel.sendTyping();
        for (int i = 1; i < args.length; i++) {
            res = res+" "+args[i];
        }
        res = res.substring(1);
        try {
            res = Unirest.get("https://montanaflynn-l33t-sp34k.p.mashape.com/encode?text=" + URLEncoder.encode(res, "UTF-8").replace("+", "%20")).header("X-Mashape-Key", FredBoat.mashapeKey).asString().getBody();
        } catch (UnirestException ex) {
            Message myMsg = TextUtils.replyWithMention(channel, invoker, " Could not connect to API! "+ex.getMessage());
            return;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        Message myMsg = channel.sendMessage(res);
        
        EventListenerBoat.messagesToDeleteIfIdDeleted.put(message.getId(), myMsg);
    }
    
}
