package net.havocmc.transport.proto.signal.bukkit;

import net.havocmc.transport.proto.BufferedObject;

import java.util.UUID;

/**
 * Created by Giovanni on 04/04/2018.
 */
public class OpenMenu04 implements BufferedObject {

    private int id;
    private UUID playerId;

    private Object[] returnPipe;

    public OpenMenu04 menu(int id) {
        this.id = id;
        return this;
    }

    public OpenMenu04 forPlayer(UUID uuid) {
        this.playerId = uuid;
        return this;
    }

    public OpenMenu04 returnWith(Object[] objects) {
        this.returnPipe = objects;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x04";
    }

    public int getMenu() {
        return id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Object[] getReturnPipe() {
        return returnPipe;
    }
}
