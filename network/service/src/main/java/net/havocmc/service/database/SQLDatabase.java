package net.havocmc.service.database;

import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;
import com.zaxxer.hikari.HikariDataSource;
import net.havocmc.service.Mercurial;
import net.havocmc.service.player.Player;
import net.havocmc.service.player.PlayerContainer;
import net.havocmc.service.player.PlayerIsland;
import net.havocmc.service.tool.SQLRunner;
import net.havocmc.transport.GloArgs;
import net.havocmc.transport.exception.NetException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

/**
 * Created by Giovanni on 27/02/2018.
 */
@SuppressWarnings("all")
public class SQLDatabase implements Database {

    private ScheduledExecutorService executorService;
    private HikariDataSource dataSource;

    private boolean running = false;

    @Override
    public Database bootstrap(LinkedTreeMap<String, Object> databaseProperties) throws NetException {
        try {
            // Rec pool = 25 for 100 then *2 each 100
            int poolSize = ((Double) databaseProperties.get("THREAD_POOL_SIZE")).intValue();
            executorService = Executors.newScheduledThreadPool(poolSize);

            dataSource = new HikariDataSource();
            dataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            dataSource.addDataSourceProperty("serverName", (String) databaseProperties.get("ADDRESS"));
            dataSource.addDataSourceProperty("port", ((Double) databaseProperties.get("PORT")).intValue());
            dataSource.addDataSourceProperty("databaseName", (String) databaseProperties.get("DATABASE_NAME"));
            dataSource.addDataSourceProperty("user", (String) databaseProperties.get("USERNAME"));
            dataSource.addDataSourceProperty("password", (String) databaseProperties.get("PASSWORD"));
            dataSource.setTransactionIsolation((String) databaseProperties.get("ISOLATION"));
            dataSource.setAutoCommit(true);

            loadTables();
        } catch (Exception e) {
            throw new NetException(e);
        }
        return this;
    }

    @Override
    public void forceIslandAt(String identifier, PlayerIsland island, @Nullable Consumer<PlayerIsland> islandFuture) {
        if (island == null) return;

        doAsynchronous(() -> {
            HashMap<String, Object> islandData = Maps.newHashMap();
            islandData.put("identifier", island.getIdentifier()); // New identifier
            islandData.put("ownerId", island.getOwner());
            islandData.put("ownerName", island.getOwnerName());
            islandData.put("members", GloArgs.gson.toJson(island.getMembers()));

            String[] keys = islandData.keySet().toArray(new String[islandData.keySet().size()]);
            String query = "UPDATE `islands` SET " + keys[0] + "= '" + islandData.get(keys[0]) + "'";

            for (int i = 1; i < islandData.keySet().size(); i++) {
                String value = keys[i];
                query += ", " + value + " = '" + islandData.get(value) + "'";
            }
            islandData.clear();

            // Set at old identifier
            query += " WHERE identifier = ?";
            Connection connection = null;
            try {
                connection = connection();
                connection.createStatement();
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, identifier);
                statement.executeUpdate();

                connection.close();

                if (islandFuture != null)
                    islandFuture.accept(island);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }, null);
    }

    @Override
    public void updateIsland(PlayerIsland island, Consumer<PlayerIsland> islandFuture) {
        if (island == null) return;

        doAsynchronous(() -> {
            String identifier = island.getIdentifier();
            HashMap<String, Object> islandData = Maps.newHashMap();
            islandData.put("identifier", identifier);
            islandData.put("ownerId", island.getOwner());
            islandData.put("ownerName", island.getOwnerName());
            islandData.put("members", GloArgs.gson.toJson(island.getMembers()));

            String[] keys = islandData.keySet().toArray(new String[islandData.keySet().size()]);
            String query = "UPDATE `islands` SET " + keys[0] + "= '" + islandData.get(keys[0]) + "'";

            for (int i = 1; i < islandData.keySet().size(); i++) {
                String value = keys[i];
                query += ", " + value + " = '" + islandData.get(value) + "'";
            }
            islandData.clear();

            query += " WHERE identifier = ?";
            Connection connection = null;
            try {
                connection = connection();
                connection.createStatement();
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, identifier);
                statement.executeUpdate();

                connection.close();

                if (islandFuture != null)
                    islandFuture.accept(island);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }, null);
    }

    @Override
    public void updatePlayer(Player player, @Nonnull Consumer<Player> playerFuture) {
        doAsynchronous(() -> {
            try {
                UUID uuid = player.getIdentifier();
                HashMap<String, Object> data = Maps.newHashMap();
                data.put("uuid", uuid.toString());
                data.put("username", player.getName());
                data.put("container", player.container() == null ? "$emtpy" : player.container().serialize());

                String[] keySet = data.keySet().toArray(new String[data.keySet().size()]);
                String query = "UPDATE `players` SET " + keySet[0] + "= '" + data.get(keySet[0]) + "'";

                for (int i = 1; i < data.keySet().size(); i++) {
                    String value = keySet[i];
                    query += ", " + value + " = '" + data.get(value) + "'";
                }
                data.clear();

                query += " WHERE uuid = ?";

                Connection connection = null;
                try {
                    connection = connection();
                    connection.createStatement();
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, uuid.toString());
                    statement.executeUpdate();

                    connection.close();
                    playerFuture.accept(player);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (connection != null)
                            connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, null);
    }

    @Override
    public void findPlayer(UUID uuid, Consumer<Optional<Player>> playerFuture) {
        doAsynchronous(() -> {
            Connection connection = null;
            ResultSet resultSet = null;

            String loadProfile = "SELECT * from `players` WHERE uuid = ?";
            try {
                connection = connection();
                PreparedStatement statement = connection.prepareStatement(loadProfile);
                statement.setString(1, uuid.toString());
                resultSet = statement.executeQuery();

                boolean nullFuture = true;

                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    PlayerContainer container = GloArgs.NO_DOUBLE_GSON.fromJson(resultSet.getString("container"), PlayerContainer.class);

                    nullFuture = false;

                    Player player = new Player(username, uuid, null);
                    player.setContainer(container);
                    playerFuture.accept(Optional.of(player));
                }

                if (nullFuture)
                    playerFuture.accept(Optional.empty());

                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                    if (resultSet != null)
                        resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, result -> {
            // Debug
            if (result.complete())
                Mercurial.getRuntime().logger().info("Result complete.");
        });
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
        if (isRunning()) return;
        Mercurial.getRuntime().logger().info("Database bootstrap..");
        loadTables();
        running = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Type getType() {
        return Type.SQL;
    }

    public Connection connection() throws SQLException {
        return dataSource.getConnection();
    }

    private void loadTables() {
        if (isRunning()) {
            Mercurial.getRuntime().logger().warning("Table loading not allowed when already connected to the SQL database.");
            return;
        }
        // We won't stop the bootstrap when an error occurs here,
        // because the tables might exist already in the database anyway.
        Mercurial.getRuntime().logger().info("Loading SQL tables, please wait..");
        File sqlFile = new File("init.sql");

        if (!sqlFile.exists()) {
            try {
                Files.copy(Mercurial.class.getResourceAsStream("/init.sql"), sqlFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String sqlFilePath = sqlFile.getPath();
        Connection connection = null;
        try {
            connection = connection();
            if (connection == null || connection.isClosed())
                throw new IllegalStateException("SQL connection is null or closed..");

            SQLRunner runner = new SQLRunner(connection, false, true);

            try {
                runner.runScript(new BufferedReader(new FileReader(new File(sqlFilePath))));
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
