package fredboat.server.webentity;

import java.util.List;

public class WebGuild {

    private final long id;
    private final String name;
    private final String icon;
    private final long owner;
    private final List<WebRole> roles;
    private final List<WebTextChannel> channels;

    public WebGuild(long id, String name, String icon, long owner, List<WebRole> roles, List<WebTextChannel> channels) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.owner = owner;
        this.roles = roles;
        this.channels = channels;
    }

    public WebGuild(String id, String name, String icon, String owner, List<WebRole> roles, List<WebTextChannel> channels) {
        this.id = Long.parseLong(id);
        this.name = name;
        this.icon = icon;
        this.owner = Long.parseLong(owner);
        this.roles = roles;
        this.channels = channels;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public long getOwner() {
        return owner;
    }

    public List<WebRole> getRoles() {
        return roles;
    }

    public List<WebTextChannel> getChannels() {
        return channels;
    }

}
