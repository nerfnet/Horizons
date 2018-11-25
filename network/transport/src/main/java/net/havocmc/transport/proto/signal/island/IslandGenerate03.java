package net.havocmc.transport.proto.signal.island;

import net.havocmc.islands.vector.IslandVector;
import net.havocmc.transport.proto.BufferedObject;

import java.util.UUID;

/**
 * Created by Giovanni on 13/03/2018.
 */
public class IslandGenerate03 implements BufferedObject {

    private UUID ownerId;
    private String islandId;
    private int x;
    private int y;
    private int z;

    public IslandGenerate03 x(int x) {
        this.x = x;
        return this;
    }

    public IslandGenerate03 y(int y) {
        this.y = y;
        return this;
    }

    public IslandGenerate03 z(int z) {
        this.z = z;
        return this;
    }

    public IslandGenerate03 forOwner(UUID ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public IslandGenerate03 generateIsland(String islandId) {
        this.islandId = islandId;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x03";
    }

    public String getIslandId() {
        return islandId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
