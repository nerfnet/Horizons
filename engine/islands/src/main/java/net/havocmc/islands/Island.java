package net.havocmc.islands;

import net.havocmc.islands.transport.TransportableIsland;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Giovanni on 28/02/2018.
 */
public interface Island extends Serializable {

    /**
     * Returns the {@link UUID} of the owner of this island.
     */
    UUID getOwner();

    /**
     * Returns the last known username of the owner of this island.
     */
    String getOwnerName();

    /**
     * Returns the identifier of this island.
     */
    String getIdentifier();

    /**
     * Returns the members of this island with their {@link IslandRank}.
     */
    HashMap<UUID, IslandProfile> getMembers();

    /**
     * Returns the unlocked {@link IslandRegion}s this island instance has.
     */
    List<IslandRegion> getAvailableRegions();

    /**
     * Returns the current visitors' {@link IslandProfile}s.
     */
    List<IslandProfile> getCurrentVisitors();

    /**
     * Returns the level of this island.
     */
    int getLevel();

    /**
     * Creates a {@link TransportableIsland}.
     */
    TransportableIsland asTransportable();
}
