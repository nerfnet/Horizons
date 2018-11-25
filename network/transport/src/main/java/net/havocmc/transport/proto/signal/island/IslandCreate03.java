package net.havocmc.transport.proto.signal.island;

import net.havocmc.transport.proto.BufferedObject;

import java.util.UUID;

/**
 * Created by Giovanni on 13/03/2018.
 */
public class IslandCreate03 implements BufferedObject {

    private UUID ownerUUID;

    public IslandCreate03 forPlayer(UUID uuid) {
        this.ownerUUID = uuid;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x03";
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }
}
