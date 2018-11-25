package net.havocmc.service.player;

import io.netty.channel.Channel;
import net.havocmc.service.Mercurial;
import net.havocmc.service.server.Server;
import net.havocmc.transport.entity.CloseableConnection;
import net.havocmc.transport.entity.ConnectionResult;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.bukkit.PlaySound04;
import net.havocmc.transport.proto.signal.bukkit.Punish04;
import net.havocmc.transport.proto.signal.bukkit.Teleport04;
import net.havocmc.transport.proto.signal.player.Message02;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by Giovanni on 25/02/2018.
 */
public class Player implements CloseableConnection<UUID, Server> {

    private final String name;
    private final UUID uniqueId;

    @Nullable
    private transient Server server;
    private PlayerContainer container;

    public Player(@Nonnull String name, @Nonnull UUID uniqueId, @Nullable Server server) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.server = server;
    }

    /**
     * Sets the {@link PlayerContainer} of this player.
     */
    public void setContainer(@Nullable PlayerContainer container) {
        this.container = container;
    }

    public PlayerContainer container() {
        return container;
    }

    /**
     * Updates the player's data and removes it gracefully from the cache.
     */
    public void updateAndClear() {
        Mercurial.runtime().database().updatePlayer(this, future -> {
            if (future.hasIsland())
                Mercurial.runtime().database().updateIsland(future.container().getIsland(), null);
            Mercurial.runtime().removePlayer(future);
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getIdentifier() {
        return uniqueId;
    }

    @Override
    public String getFullIdentifier() {
        return getName() + "@" + getIdentifier();
    }

    @Override
    public ConnectionResult attemptRead(Server connection, Channel previousChannel) {
        return connection.attemptRead(this, previousChannel);
    }

    @Override
    public void reject(String context, Channel writeTo) {
        Punish04 punish04 = new Punish04()
                .player(uniqueId)
                .data("UPDATE::kick@" + context);
        writeTo.writeAndFlush(punish04);
    }

    @Override
    public void write(BufferedObject object) {
        if (server == null) return;
        server.write(object);
    }

    public boolean hasIsland() {
        return container != null && container.getIsland() != null;
    }

    /**
     * Sends a {@link Teleport04} {@link BufferedObject} to the {@link Server} this player is on,
     * which then handles the teleportation request.
     */
    public Player teleport(String world, Vector<Double> vector) {
        Teleport04 teleport04 = new Teleport04()
                .player(uniqueId)
                .location(world, vector);
        write(teleport04);
        return this;
    }

    /**
     * Sends a {@link Message02} {@link BufferedObject} to the {@link Server} this player is on,
     * which then translates it to a Bukkit message, supports colour codes.
     */
    public Player message(String... messages) {
        Message02 message02 = new Message02()
                .forPlayer(uniqueId)
                .withMessage(messages);
        write(message02);
        return this;
    }

    /**
     * Sends a {@link PlaySound04} {@link BufferedObject} to the {@link Server} this player is on,
     * which then plays the sound for the player.
     */
    public Player playSound(String soundName, float volume, float pitch) {
        PlaySound04 sound04 = new PlaySound04()
                .forPlayer(uniqueId)
                .sound(soundName)
                .volume(volume)
                .pitch(pitch);
        write(sound04);
        return this;
    }

    public boolean onServer(@Nonnull Server server) {
        return this.server != null && this.server.getName().equalsIgnoreCase(server.getName());
    }

    public void setServer(@Nullable Server server) {
        this.server = server;
    }

    @Override
    public void close() {
        if (server == null) {
            Mercurial.getRuntime().logger().warning("Failed to close a Player connection, no parent connection.");
            return;
        }

        updateAndClear();

        reject("&cDisconnected gracefully.", server.channel());

        if (server.searchPlayer(uniqueId).isPresent())
            server.remove(this);

        server = null;
    }
}
