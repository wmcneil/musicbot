package fredboat.command.moderation;

import fredboat.commandmeta.abs.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.List;

public class SoftbanCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {

    }

    private boolean isAuthorized(Member mod, Member target) {
        if(mod.isOwner()) return true;

        if(!PermissionUtil.checkPermission(mod.getGuild(), mod, Permission.BAN_MEMBERS, Permission.KICK_MEMBERS)) return false;
        
        if(Role roles mod.getRoles())
    }

    private int getHighestRole(List<Role> roles) {

    }

}
