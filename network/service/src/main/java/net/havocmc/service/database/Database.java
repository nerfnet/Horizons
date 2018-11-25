package net.havocmc.service.database;

import com.google.gson.internal.LinkedTreeMap;
import net.havocmc.service.player.Player;
import net.havocmc.service.player.PlayerIsland;
import net.havocmc.transport.exception.NetException;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Giovanni on 27/02/2018.
 */
public interface Database {

    /**
     * Starts the database service.
     */
    Database bootstrap(LinkedTreeMap<String, Object> databaseProperties) throws NetException;

    /**
     * Force updates a {@link PlayerIsland} in the database by copying the island and setting it at the given identifier.
     *
     * @param island     The island to copy.
     * @param identifier The identifier of the new island.
     */
    void forceIslandAt(String identifier, PlayerIsland island, @Nullable Consumer<PlayerIsland> islandFuture);

    /**
     * Updates a {@link PlayerIsland} from a {@link Player#container} in the database.
     */
    void updateIsland(PlayerIsland island, @Nullable Consumer<PlayerIsland> islandFuture);

    /**
     * Updates a {@link Player#container()} in the database.
     *
     * @param playerFuture Future action for the {@link Player}.
     */
    void updatePlayer(Player player, Consumer<Player> playerFuture);

    /**
     * Finds a {@link Player} in the database with a future {@link Consumer<Optional>}.
     */
    void findPlayer(UUID uuid, Consumer<Optional<Player>> playerFuture);

    /**
     * Returns whether a {@link Player} is existent in the database, by UUID.
     */
    boolean playerExists(UUID uuid);

    /**
     * Returns the database {@link Type}.
     */
    Type getType();

    /**
     * Performs a task asynchronously and returns a future {@link AsyncResult} of the task.
     */
    void doAsynchronous(Runnable runnable, @Nullable Consumer<AsyncResult> resultConsumer);

    /**
     * Performs the initiation of the database.
     */
    void init();

    /**
     * Returns whether the database is running or not.
     */
    boolean isRunning();

    enum Type {

        SQL, MONGO;
    }
}
