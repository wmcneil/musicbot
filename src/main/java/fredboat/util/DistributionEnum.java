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
