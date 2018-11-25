package net.havocmc.transport.proto.signal.bukkit;

import net.havocmc.transport.proto.BufferedObject;

import java.util.UUID;

/**
 * Created by Giovanni on 04/03/2018.
 */
public class Punish04 implements BufferedObject {

    private UUID playerUUID;
    private String data;


    public Punish04 player(UUID uuid) {
        this.playerUUID = uuid;
        return this;
    }

    public Punish04 data(String data) {
        this.data = data;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x04";
    }

    public UUID getPlayer() {
        return playerUUID;
    }

    public String getData() {
        return data;
    }
}
