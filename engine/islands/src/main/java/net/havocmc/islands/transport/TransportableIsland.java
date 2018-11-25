package net.havocmc.islands.transport;

import net.havocmc.islands.Island;
import net.havocmc.islands.IslandProfile;
import net.havocmc.islands.IslandRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giovanni on 10/03/2018.
 */
public class TransportableIsland {

    private int gems;
    private int level;
    private List<IslandProfile> profiles;
    private List<IslandRegion> availableRegions;
    private String ownerName;

    public TransportableIsland create(Island island) {
        level = island.getLevel();
        profiles = new ArrayList<>(island.getMembers().values());
        availableRegions = island.getAvailableRegions();
        ownerName = island.getOwnerName();

        return this;
    }

    public int getGems() {
        return gems;
    }

    public int getLevel() {
        return level;
    }

    public List<IslandProfile> getProfiles() {
        return profiles;
    }

    public List<IslandRegion> getAvailableRegions() {
        return availableRegions;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
