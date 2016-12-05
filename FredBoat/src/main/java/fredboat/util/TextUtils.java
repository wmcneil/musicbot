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

package fredboat.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.FredBoat;
import fredboat.commandmeta.MessagingException;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import org.slf4j.LoggerFactory;

public class TextUtils {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(TextUtils.class);

    private TextUtils() {
    }

    public static Message prefaceWithMention(Member member, String msg) {
        MessageBuilder builder = new MessageBuilder().append(member).append(msg);
        return builder.build();
    }

    public static Message replyWithMention(TextChannel channel, Member member, String msg) {
        MessageBuilder builder = new MessageBuilder().append(member).append(msg);
        Message mes = builder.build();
        channel.sendMessage(mes).queue();
        return mes;
    }

    public static void handleException(Throwable e, MessageChannel channel) {
        handleException(e, channel, null);
    }

    public static void handleException(Throwable e, MessageChannel channel, Member invoker) {
        if (e instanceof MessagingException) {
            channel.sendMessage(invoker.getEffectiveName() + ": " + e.getMessage()).queue();
            return;
        }

        log.error("Caught exception while executing a command", e);

        MessageBuilder builder = new MessageBuilder();

        if (invoker != null) {
            builder.append(invoker);

            String filtered = " an error occured :anger: ```java\n" + e.toString() + "\n";

            for (String str : FredBoat.getGoogleKeys()) {
                filtered = filtered.replace(str, "GOOGLE_SERVER_KEY");
            }

            builder.append(filtered);
        } else {
            String filtered = "An error occured :anger: ```java\n" + e.toString() + "\n";

            for (String str : FredBoat.getGoogleKeys()) {
                filtered = filtered.replace(str, "GOOGLE_SERVER_KEY");
            }

            builder.append(filtered);
        }

        //builder.appendString("```java\n");
        for (StackTraceElement ste : e.getStackTrace()) {
            builder.append("\t" + ste.toString() + "\n");
            if ("prefixCalled".equals(ste.getMethodName())) {
                break;
            }
        }
        builder.append("\t...```");

        try {
            channel.sendMessage(builder.build()).queue();
        } catch (UnsupportedOperationException tooLongEx) {
            channel.sendMessage("An error occured :anger: Error was too long to display.").queue();
        }
    }

    public static String postToHastebin(String body) throws UnirestException {
        return Unirest.post("http://hastebin.com/documents").body(body).asJson().getBody().getObject().getString("key");
    }

    public static String postToHastebin(String body, boolean asURL) throws UnirestException {
        if (asURL) {
            return "http://hastebin.com/" + postToHastebin(body);
        } else {
            return postToHastebin(body);
        }
    }

    public static String formatTime(long millis) {
        if (millis == Long.MAX_VALUE) {
            return "LIVE";
        }

        long t = millis / 1000L;
        int sec = (int) (t % 60L);
        int min = (int) ((t % 3600L) / 60L);
        int hrs = (int) (t / 3600L);

        String timestamp;

        if (hrs != 0) {
            timestamp = forceTwoDigits(hrs) + ":" + forceTwoDigits(min) + ":" + forceTwoDigits(sec);
        } else {
            timestamp = forceTwoDigits(min) + ":" + forceTwoDigits(sec);
        }

        return timestamp;
    }

    private static String forceTwoDigits(int i) {
        return i < 10 ? "0" + i : Integer.toString(i);
    }
}
