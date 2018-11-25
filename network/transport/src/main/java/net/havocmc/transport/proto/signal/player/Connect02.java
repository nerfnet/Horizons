package net.havocmc.transport.proto.signal.player;

import net.havocmc.transport.proto.BufferedObject;

import java.util.UUID;

/**
 * Created by Giovanni on 27/02/2018.
 */
public class Connect02 implements BufferedObject {

    private UUID playerUUID;
    private String connectData;

    public Connect02 withPlayer(UUID uuid) {
        this.playerUUID = uuid;
        return this;
    }

    public Connect02 syncData(String connectData) {
        this.connectData = connectData;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x02";
    }

    public UUID getUniqueId() {
        return playerUUID;
    }

    public String getData() {
        return connectData;
    }
}
