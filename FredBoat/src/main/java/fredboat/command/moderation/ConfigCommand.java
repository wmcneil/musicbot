package fredboat.command.moderation;

import fredboat.commandmeta.abs.Command;
import fredboat.db.EntityReader;
import fredboat.db.EntityWriter;
import fredboat.db.entities.GuildConfig;
import fredboat.feature.I18n;
import fredboat.util.BotConstants;
import fredboat.util.TextUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.text.MessageFormat;

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
                .append(MessageFormat.format(I18n.get(guild).getString("configNoArgs") + "\n", guild.getName()))
                .append("track_announce = ").append(gc.isTrackAnnounce()).append("\n")
                .append("auto_resume = ").append(gc.isAutoResume()).append("\n")
                .append("```");

        channel.sendMessage(mb.build()).queue();
    }

    private void setConfig(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        if(!PermissionUtil.checkPermission(guild, invoker, Permission.ADMINISTRATOR)
                && !invoker.getUser().getId().equals(BotConstants.OWNER_ID)){
            channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configNotAdmin"), invoker.getEffectiveName())).queue();
            return;
        }

        if(args.length != 3) {
            channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configUsage"), invoker.getEffectiveName())).queue();
            return;
        }

        GuildConfig gc = EntityReader.getGuildConfig(guild.getId());
        String key = args[1];
        String val = args[2];

        switch (key) {
            case "track_announce":
                if (val.equalsIgnoreCase("true") | val.equalsIgnoreCase("false")) {
                    gc.setTrackAnnounce(Boolean.valueOf(val));
                    TextUtils.replyWithName(channel, invoker, "`track_announce`" + MessageFormat.format(I18n.get(guild).getString("configSetTo"), val));
                    EntityWriter.mergeGuildConfig(gc);
                } else {
                    channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configMustBeBoolean"), invoker.getEffectiveName())).queue();
                }
                break;
            case "auto_resume":
                if (val.equalsIgnoreCase("true") | val.equalsIgnoreCase("false")) {
                    gc.setAutoResume(Boolean.valueOf(val));
                    TextUtils.replyWithName(channel, invoker, "`auto_resume`" + MessageFormat.format(I18n.get(guild).getString("configSetTo"), val));
                    EntityWriter.mergeGuildConfig(gc);
                } else {
                    channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configMustBeBoolean"), invoker.getEffectiveName())).queue();
                }
                break;
            default:
                channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configUnknownKey"), invoker.getEffectiveName())).queue();
                break;
        }
    }
}
