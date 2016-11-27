package fredboat.common.db.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_config")
public class UConfig {

    @Id
    private long userId;
    private String bearer;
    private String refresh;
    private long bearerExpiration; //Unix epoch milliseconds

    public String getBearer() {
        return bearer;
    }

    public String getRefresh() {
        return refresh;
    }

    public long getUserId() {
        return userId;
    }

    public long getBearerExpiration() {
        return bearerExpiration;
    }

    public UConfig setBearer(String bearer) {
        this.bearer = bearer;
        return this;
    }

    public UConfig setRefresh(String refresh) {
        this.refresh = refresh;
        return this;
    }

    public UConfig setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public UConfig setBearerExpiration(long bearerExpiration) {
        this.bearerExpiration = bearerExpiration;
        return this;
    }

}
