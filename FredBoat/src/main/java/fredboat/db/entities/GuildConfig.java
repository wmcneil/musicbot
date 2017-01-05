package fredboat.db.entities;

import fredboat.db.DatabaseManager;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.persistence.*;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

@Entity
@Table(name = "guild_config")
public class GuildConfig {

    @Id
    private String guildId;

    @Column(name = "track_announce", nullable = false)
    private boolean trackAnnounce = false;

    @Column(name = "autoplay", nullable = false)
    private boolean autoplay = false;

    public GuildConfig() {
    }

    public GuildConfig(String id) {
        this.guildId = id;
    }

    public boolean isTrackAnnounce() {
        return trackAnnounce;
    }

    public void setTrackAnnounce(boolean trackAnnounce) {
        this.trackAnnounce = trackAnnounce;
    }

    public boolean isAutoplay() {
        return autoplay;
    }

    public void setAutoplay(boolean autoplay) {
        this.autoplay = autoplay;
    }

    /*@OneToMany
    @JoinColumn(name = "guildconfig")
    private Set<TCConfig> textChannels;

    public GuildConfig(Guild guild) {
        this.guildId = Long.parseLong(guild.getId());

        textChannels = new CopyOnWriteArraySet<>();

        for (TextChannel tc : guild.getTextChannels()) {
            TCConfig tcc = new TCConfig(this, tc);
            textChannels.add(tcc);
        }

        for (TCConfig tcc : textChannels) {
            DatabaseManager.getEntityManager().persist(tcc);
        }
    }

    public Set<TCConfig> getTextChannels() {
        return textChannels;
    }
    
    public void addTextChannel(TCConfig tcc){
        textChannels.add(tcc);
    }
    
    public void removeTextChannel(TCConfig tcc){
        textChannels.remove(tcc);
    }

    public long getGuildId() {
        return guildId;
    }
    */

}
