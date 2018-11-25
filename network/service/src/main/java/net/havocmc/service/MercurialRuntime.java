package net.havocmc.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.internal.LinkedTreeMap;
import net.havocmc.service.database.Database;
import net.havocmc.service.database.MongoDatabase;
import net.havocmc.service.database.SQLDatabase;
import net.havocmc.service.island.IslandRuntime;
import net.havocmc.service.player.Player;
import net.havocmc.service.player.PlayerIsland;
import net.havocmc.service.server.Server;
import net.havocmc.service.signal.Proto01Transporter;
import net.havocmc.service.signal.Proto02Transporter;
import net.havocmc.service.signal.Proto03Transporter;
import net.havocmc.service.signal.Proto04Transporter;
import net.havocmc.transport.RuntimeWrapper;
import net.havocmc.transport.bootstrap.BootstrapProperties;
import net.havocmc.transport.bootstrap.ShutdownHook;
import net.havocmc.transport.exception.NetException;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by Giovanni on 25/02/2018.
 */
@SuppressWarnings("all")
public class MercurialRuntime extends RuntimeWrapper<BootstrapProperties> {

    public static String MC_MESSAGE_PREFIX = "&6&lHORIZONS";
    private final List<Transporter<?, ?>> transporters = Lists.newArrayList();
    private final List<BufferedObject> objectQueue = Lists.newArrayList();

    /* IDENTIFIER, SERVER */
    private final ConcurrentHashMap<String, Server> serverConnections = new ConcurrentHashMap<>();

    /* IDENTIFIER, PLAYER */
    private final ConcurrentHashMap<UUID, Player> playerConnections = new ConcurrentHashMap<>();
    private final IslandRuntime islandRuntime;
    private Database database;

    MercurialRuntime(InetSocketAddress socketAddress, BootstrapProperties properties) {
        super(socketAddress, properties);

        islandRuntime = new IslandRuntime();
        islandRuntime.load();

        LinkedTreeMap<String, Object> minecraftProperties = properties.read("MINECRAFT", LinkedTreeMap.class);
        MC_MESSAGE_PREFIX = (String) minecraftProperties.get("MESSAGE_PREFIX");

        Stream.of(
                new Proto01Transporter(this),
                new Proto02Transporter(this),
                new Proto03Transporter(this),
                new Proto04Transporter(this)
        ).forEach(transporters::add);
    }

    public void databaseBootstrap() {
        LinkedTreeMap<String, Object> databaseProperties = getProperties().read("DATABASE", LinkedTreeMap.class);
        String databaseType = (String) databaseProperties.get("TYPE");

        try {
            if (databaseType.equalsIgnoreCase("sql"))
                database = new SQLDatabase().bootstrap(databaseProperties);
            else database = new MongoDatabase(null);
        } catch (NetException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void forceExit() {
        if (!runtimeAvailable()) return;
        islandRuntime.close();

        logger().info("Stopping all servers..");
        serverConnections.values().forEach(server -> {
            server.close();
        }); // Moved outside of ShutdownHook @ 01/04/2018 to make space for program exit.

        try {
            exit(new ShutdownHook(this, false) {
                @Override
                public void consume() {
                    System.exit(0);
                }
            });
        } catch (NetException e) {
            logger().warning("Default exit failure, expecting crash so force exit.");
            System.exit(0);
        }
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
     * Returns whether a {@link Server} is connected, by name.
     */
    public boolean serverConnected(String name) {
        for (Server server : serverConnections.values()) {
            if (server.getName().equalsIgnoreCase(name) && server.isRunning())
                return true;
        }
        return false;
    }

    /**
     * Returns whether a {@link  Server} is connected, by {@link Server#getIdentifier()}.
     */
    public boolean serverConnectedId(String id) {
        return serverConnections.containsKey(id) && serverConnections.get(id).isRunning();
    }

    /**
     * Adds a {@link Server} to the map, does not invoke bootstrap.
     */
    public void addServer(Server server) {
        if (serverConnectedId(server.getIdentifier())) return;
        serverConnections.put(server.getIdentifier(), server);
    }

    /**
     * Removes a {@link Server} from the connection map.
     */
    public void removeServer(Server server) {
        if (!serverConnected(server.getIdentifier())) return;
        serverConnections.remove(server.getIdentifier());
    }

    /**
     * Finds a {@link Server} by name, does not check whether it's online or not.
     */
    public Optional<Server> findServer(String name) {
        if (!serverConnected(name)) return Optional.empty();

        for (Server server : serverConnections.values()) {
            if (server.getName().equalsIgnoreCase(name))
                return Optional.of(server);
        }
        return Optional.empty();
    }

    /**
     * Returns whether a {@link Player} is connected, by unique id.
     */
    public boolean playerConnected(UUID uuid) {
        return playerConnections.containsKey(uuid);
    }

    /**
     * Returns whether a {@link Player} is connected, by name.
     */
    public boolean playerConnected(String name) {
        for (Player player : playerConnections.values()) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a {@link Player} to the player map.
     */
    public void addPlayer(Player player) {
        if (playerConnected(player.getIdentifier())) return;
        playerConnections.put(player.getIdentifier(), player);
    }

    /**
     * Finds a {@link Player} by UUID.
     */
    public Optional<Player> findPlayer(UUID uuid) {
        if (!playerConnected(uuid)) return Optional.empty();

        for (Player player : playerConnections.values()) {
            if (player.getIdentifier().toString().equals(uuid.toString())) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    /**
     * Sends a {@link BufferedObject} to every online {@link Player}.
     */
    public void writeToAllPlayers(BufferedObject object) {
        playerConnections.values().forEach(player -> {
            player.write(object);
        });
    }

    /**
     * Returns an {@link ImmutableList} of all online {@link Player}.
     * @return
     */
    public ImmutableList<Player> allPlayers() {
        return ImmutableList.copyOf(playerConnections.values());
    }

    /**
     * Removes a {@link Player} from the player map.
     */
    public void removePlayer(Player player) {
        if (!playerConnected(player.getIdentifier())) return;
        playerConnections.remove(player.getIdentifier());
    }

    /**
     * Queues a {@link BufferedObject} so it can be sent in the future.
     */
    public void queue(BufferedObject object) {
        int maxSize = getProperties().read("MAX_OBJECT_QUEUE", Double.class).intValue();
        if (objectQueue.size() >= maxSize) return;

        logger().info(object.toString() + " is being queued at " + System.currentTimeMillis());
        objectQueue.add(object);
    }

    /**
     * Returns an {@link Optional<PlayerIsland>} by searching for all live islands by {@link PlayerIsland#getIdentifier()}
     */
    public Optional<PlayerIsland> findIsland(String id) {
        for (Player player : playerConnections.values()) {
            if (!player.hasIsland()) continue;
            if (player.container().getIsland().getIdentifier().equalsIgnoreCase(id)) {
                return Optional.of(player.container().getIsland());
            }
        }
        return Optional.empty();
    }

    /**
     * Returns an {@link Optional<PlayerIsland>} by searching for all live islands by {@link PlayerIsland#getOwner()} ()}
     */
    public Optional<PlayerIsland> findIsland(UUID ownerId) {
        if (!playerConnected(ownerId)) return Optional.empty();
        Player player = playerConnections.get(ownerId);
        return Optional.ofNullable(player.container().getIsland());
    }

    public ImmutableList<PlayerIsland> findLiveIslandsOf(UUID member) {
        List<PlayerIsland> islands = Lists.newArrayList();

        for (Player player : playerConnections.values()) {
            if (!player.hasIsland()) continue;
            PlayerIsland island = player.container().getIsland();
            if (island.getMembers().containsKey(member))
                islands.add(island);
        }
        return ImmutableList.copyOf(islands);
    }

    public Database database() {
        return database;
    }

    public IslandRuntime islandRuntime() {
        return islandRuntime;
    }
}
