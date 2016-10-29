package fredboat.util;

import java.util.ArrayList;
import java.util.List;

public enum DistributionEnum {
    MAIN(3400, "production"), MUSIC(3500, "music"), BETA(3600, "beta"), PATRON(3700, "patron");

    private final int shard0Port;
    private final String id;

    private DistributionEnum(int shard0Port, String id) {
        this.shard0Port = shard0Port;
        this.id = id;
    }

    public String getUrlForShard(int shardId) {
        switch (this) {
            case BETA:
                return "http://localhost:" + (shard0Port + shardId) + "/";
            case MAIN:
                return "http://fb" + shardId + ".frederikam.com:" + getPort(shardId) + "/";
            case MUSIC:
                return "http://fbm" + shardId + ".frederikam.com:" + getPort(shardId) + "/";
            default:
                throw new IllegalArgumentException("Not a valid distribution type");
        }
    }

    public List<String> getUrlsForOtherShard(int shardId, int numShards) {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < numShards; i++) {
            if (i == shardId) {
                continue;
            }

            list.add(this.getUrlForShard(i));
        }

        return list;
    }

    public int getPort(int shardId) {
        return this.shard0Port + shardId;
    }

    public String getId() {
        return id;
    }

}
