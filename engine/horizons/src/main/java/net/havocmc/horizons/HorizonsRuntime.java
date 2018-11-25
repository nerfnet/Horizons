package net.havocmc.horizons;

import com.google.common.collect.Lists;
import com.google.gson.internal.LinkedTreeMap;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.horizons.network.Proto01Transporter;
import net.havocmc.horizons.network.Proto02Transporter;
import net.havocmc.horizons.network.Proto03Transporter;
import net.havocmc.horizons.network.Proto04Transporter;
import net.havocmc.transport.RuntimeWrapper;
import net.havocmc.transport.bootstrap.BootstrapProperties;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by Giovanni on 03/03/2018.
 */
@SuppressWarnings("unchecked")
public class HorizonsRuntime extends RuntimeWrapper<BootstrapProperties> {

    public static String HORIZONS_PREFIX = "&6&lHORIZONS";
    private final List<Transporter<?, ?>> transporters = Lists.newArrayList();
    private final List<BufferedObject> objectQueue = Lists.newArrayList();
    private final ConcurrentHashMap<UUID, IslandPlayer> playerConnections = new ConcurrentHashMap<>();
    private boolean closing = false;

    HorizonsRuntime(InetSocketAddress socketAddress, BootstrapProperties properties) {
        super(socketAddress, properties);

        LinkedTreeMap<String, Object> minecraftProperties = properties.read("MINECRAFT", LinkedTreeMap.class);
        HORIZONS_PREFIX = (String) minecraftProperties.get("MESSAGE_PREFIX");

        Stream.of(
                new Proto01Transporter(this),
                new Proto02Transporter(this),
                new Proto03Transporter(this),
                new Proto04Transporter(this)
        ).forEach(transporters::add);
    }

    @Override
    public Transporter<?, ?>[] poolArray() {
        return transporters.toArray(new Transporter[0]);
    }

    @Override
    public BufferedObject[] objectsInQueue() {
        synchronized (objectQueue) {
            return objectQueue.toArray(new BufferedObject[0]);
        }
    }

    /**
     * Returns whether a {@link IslandPlayer} is connected, by unique id.
     */
    public boolean playerConnected(UUID uuid) {
        return playerConnections.containsKey(uuid);
    }

    /**
     * Returns whether a {@link IslandPlayer} is connected, by name.
     */
    public boolean playerConnected(String name) {
        for (IslandPlayer islandPlayer : playerConnections.values()) {
            if (islandPlayer.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a {@link IslandPlayer} to the IslandPlayer map.
     */
    public void addIslandPlayer(IslandPlayer IslandPlayer) {
        if (playerConnected(IslandPlayer.getIdentifier())) return;
        playerConnections.put(IslandPlayer.getIdentifier(), IslandPlayer);
    }

    /**
     * Finds a {@link IslandPlayer} by UUID.
     */
    public Optional<IslandPlayer> findIslandPlayer(UUID uuid) {
        if (!playerConnected(uuid)) return Optional.empty();

        for (IslandPlayer islandPlayer : playerConnections.values()) {
            if (islandPlayer.getIdentifier().toString().equals(uuid.toString())) {
                return Optional.of(islandPlayer);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns a {@link Collection<IslandPlayer>} which are currently online.
     */
    public Collection<IslandPlayer> allPlayers() {
        return playerConnections.values();
    }

    /**
     * Returns whether there are no connections.
     */
    public boolean noPlayers() {
        return playerConnections.isEmpty();
    }

    public boolean isClosing() {
        return closing;
    }

    public void setClosing(boolean closing) {
        this.closing = closing;
    }
}
