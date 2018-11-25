package net.havocmc.islands;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Giovanni on 01/03/2018.
 * <p>
 * An {@link Island} profile for players, used for offline player interactions.
 */
public class IslandProfile implements Serializable {

    private UUID uniqueId;
    private String name;
    private IslandRank rank;

    public IslandProfile(UUID uniqueId, String name, IslandRank rank) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.rank = rank;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public IslandRank getRank() {
        return rank;
    }

    public void setRank(IslandRank rank) {
        this.rank = rank;
    }
}
