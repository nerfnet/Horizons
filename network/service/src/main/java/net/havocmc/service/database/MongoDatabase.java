package net.havocmc.service.database;

import com.google.gson.internal.LinkedTreeMap;
import net.havocmc.service.player.Player;
import net.havocmc.service.player.PlayerIsland;
import net.havocmc.transport.bootstrap.BootstrapProperties;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

/**
 * Created by Giovanni on 27/02/2018.
 */
@SuppressWarnings("all")
public class MongoDatabase implements Database {

    private final ScheduledExecutorService executorService;

    public MongoDatabase(BootstrapProperties properties) {

        this.executorService = Executors.newScheduledThreadPool(properties.read("THREAD_POOL_SIZE", Double.class).intValue());
        LinkedTreeMap<String, Object> databaseProperties = properties.read("DATABASE", LinkedTreeMap.class);
    }

    @Override
    public Database bootstrap(LinkedTreeMap<String, Object> databaseProperties) {
        return null;
    }

    @Override
    public void forceIslandAt(String identifier, PlayerIsland island, @Nullable Consumer<PlayerIsland> islandFuture) {

    }

    @Override
    public void updateIsland(PlayerIsland island, Consumer<PlayerIsland> islandFuture) {

    }

    @Override
    public void updatePlayer(Player player, Consumer<Player> playerConsumer) {

    }

    @Override
    public void findPlayer(UUID uuid, Consumer<Optional<Player>> playerFuture) {

    }

    @Override
    public boolean playerExists(UUID uuid) {
        return false;
    }

    @Override
    public void doAsynchronous(Runnable runnable, @Nullable Consumer<AsyncResult> resultConsumer) {
        AsyncResult result = new AsyncResult() {
            private boolean complete = false;

            @Override public void end() {
                complete = true;
            }

            @Override
            public boolean complete() {
                return complete;
            }

            @Override
            public Runnable getPast() {
                return runnable;
            }
        };

        executorService.execute(() -> {
            runnable.run();
            result.end();
            if (resultConsumer != null)
                resultConsumer.accept(result);
        });
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Type getType() {
        return Type.MONGO;
    }
}
