package fredboat.server.webentity;

import fredboat.common.db.entities.UConfig;

public class WebUser {

    private final long id;
    private final String name;
    private final String discriminator;
    private final String avatar;
    private final boolean bot;
    private UConfig config = null;

    public WebUser(long id, String name, String discriminator, String avatar, boolean bot) {
        this.id = id;
        this.name = name;
        this.discriminator = discriminator;
        this.avatar = avatar;
        this.bot = bot;
    }
    
    public WebUser(String id, String name, String discriminator, String avatar, boolean bot) {
        this.id = Long.parseLong(id);
        this.name = name;
        this.discriminator = discriminator;
        this.avatar = avatar;
        this.bot = bot;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isBot() {
        return bot;
    }

    public UConfig getConfig() {
        return config;
    }

    public void setConfig(UConfig config) {
        this.config = config;
    }

}
