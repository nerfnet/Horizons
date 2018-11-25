package net.havocmc.transport.proto.signal.island;

import net.havocmc.transport.proto.BufferedObject;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Giovanni on 07/04/2018.
 */
public class IslandAction03<E extends Serializable> implements BufferedObject {

    private UUID playerId;
    private String action;
    private E type;

    public IslandAction03<E> holdGeneric(E type) {
        this.type = type;
        return this;
    }

    public IslandAction03<E> forPlayer(UUID uuid) {
        this.playerId = uuid;
        return this;
    }

    public IslandAction03<E> doAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x03";
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getAction() {
        return action;
    }

    public E getType() {
        return type;
    }
}
