package net.havocmc.transport.proto.signal.bukkit;

import net.havocmc.transport.proto.BufferedObject;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by Giovanni on 02/03/2018.
 */
public class Teleport04 implements BufferedObject {

    private UUID playerUUID;
    private String world;
    private Vector<Double> vector;

    public Teleport04 player(@Nonnull UUID uuid) {
        this.playerUUID = uuid;
        return this;
    }

    public Teleport04 location(@Nonnull String world, @Nonnull Vector<Double> vector) {
        this.vector = vector;
        this.world = world;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x04";
    }

    public Vector<Double> getVector() {
        return vector;
    }

    public UUID getPlayer() {
        return playerUUID;
    }

    public String getWorld() {
        return world;
    }
}
