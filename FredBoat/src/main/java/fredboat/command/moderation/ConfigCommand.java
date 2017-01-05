package fredboat.command.moderation;

import fredboat.commandmeta.abs.Command;
import fredboat.db.EntityReader;
import fredboat.db.EntityWriter;
import fredboat.db.entities.GuildConfig;
import fredboat.util.BotConstants;
import fredboat.util.TextUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class ConfigCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        if(args.length == 1) {
            printConfig(guild, channel, invoker, message, args);
        } else {
            setConfig(guild, channel, invoker, message, args);
        }
    }

    private void printConfig(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildConfig gc = EntityReader.getGuildConfig(guild.getId());

        MessageBuilder mb = new MessageBuilder()
                .append("Configuration for **" + guild.getName() + "**:```\n")
                .append("track_announce = ")
                .append(gc.isTrackAnnounce())
                .append("\n```");

        channel.sendMessage(mb.build()).queue();
    }

    private void setConfig(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        if(!PermissionUtil.checkPermission(guild, invoker, Permission.ADMINISTRATOR)
                && !invoker.getUser().getId().equals(BotConstants.OWNER_ID)){
            channel.sendMessage(invoker.getEffectiveName() + ": You need to be an administrator in order to alter server configuration.").queue();
            return;
        }

        if(args.length != 3) {
            channel.sendMessage(invoker.getEffectiveName() + ": Proper usage:\n;;config\nconfig <key> <value>").queue();
            return;
        }

        GuildConfig gc = EntityReader.getGuildConfig(guild.getId());
        String key = args[1];
        String val = args[2];

        if(key.equals("track_announce")) {
            if(val.equalsIgnoreCase("true") | val.equalsIgnoreCase("false")) {
                gc.setTrackAnnounce(Boolean.valueOf(val));
                TextUtils.replyWithName(channel, invoker, "`track_announce` is now set to `"+val+"`.");
                EntityWriter.mergeGuildConfig(gc);
            } else {
                channel.sendMessage(invoker.getEffectiveName() + ": This value must be true or false.").queue();
            }
        } else {
            channel.sendMessage(invoker.getEffectiveName() + ": Unknown key.").queue();
        }
    }
}
