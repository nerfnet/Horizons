package net.havocmc.transport.proto.signal.island;

import net.havocmc.transport.proto.BufferedObject;

import javax.annotation.Nonnull;

/**
 * Created by Giovanni on 02/03/2018.
 */
public class RegionUpdate03 implements BufferedObject {

    private String islandId;
    private String updateData;

    public RegionUpdate03 onIsland(@Nonnull String islandId) {
        this.islandId = islandId;
        return this;
    }

    public RegionUpdate03 withUpdate(String updateData) {
        this.updateData = updateData;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x03";
    }

    public String getIslandId() {
        return islandId;
    }

    public String getUpdateData() {
        return updateData;
    }
}
