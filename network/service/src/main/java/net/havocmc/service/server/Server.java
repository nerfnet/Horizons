package net.havocmc.service.server;

import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import net.havocmc.service.Mercurial;
import net.havocmc.service.player.Player;
import net.havocmc.transport.bootstrap.BootstrapProperties;
import net.havocmc.transport.entity.CloseableConnection;
import net.havocmc.transport.entity.Connection;
import net.havocmc.transport.entity.ConnectionResult;
import net.havocmc.transport.exception.NetException;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.connection.Exit01;
import net.havocmc.transport.proto.signal.player.Connect02;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Giovanni on 25/02/2018.
 */
@SuppressWarnings("unchecked")
public class Server implements CloseableConnection<String, Player> {

    private final String name;
    private final UUID uuid = UUID.randomUUID();

    private final CopyOnWriteArrayList<Player> onlinePlayers = Lists.newCopyOnWriteArrayList();

    private Channel channel;
    private boolean available = false;
    private int maxPlayers = 100;

    public Server(@Nonnull String name, BootstrapProperties properties) {
        this.name = name;

        // Due to transportation this is a LinkedHashMap, not a LinkedTreeMap.
        LinkedHashMap<String, Object> serverProperties = properties.read("MINECRAFT", LinkedHashMap.class);
        this.maxPlayers = ((Double) serverProperties.get("MAX_PLAYERS")).intValue();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIdentifier() {
        return name + uuid.toString().split("-")[0];
    }

    @Override
    public String getFullIdentifier() {
        return getName() + "@" + uuid.toString().replaceAll("-", "");
    }

    @Override
    public ConnectionResult attemptRead(Player connection, Channel previousChannel) {
        if (!isRunning()) {
            return new ConnectionResult() {
                @Override
                public boolean success() {
                    return false;
                }

                @Override public ConnectionResult complete() {
                    connection.reject("&6&lHORIZONS _NL_ &cServer is offline", previousChannel);
                    return this;
                }
            };
        }

        return new ConnectionResult() {
            private boolean status = false;

            @Override public boolean success() {
                return status;
            }

            @Override public ConnectionResult complete() {
                if (onlinePlayers.size() >= maxPlayers) {
                    connection.reject("&6&lHORIZONS _NL_ &cThis server is full!", previousChannel);
                    status = false;
                } else {
                    System.out.println("[deb-lazy] Reading connection..");
                    onlinePlayers.add(connection);
                    status = true;

                    Connect02 connect02 = new Connect02()
                            .withPlayer(connection.getIdentifier())
                            .syncData("SYNC::connect@" + getFullIdentifier());

                    System.out.println(connect02.getUniqueId().toString());

                    connection.setServer(lazy());
                    connection.write(connect02);
                }
                return this;
            }
        };
    }

    @Override
    public void reject(String context, Channel writeTo) {

    }

    @Override
    public void write(BufferedObject object) {
        channel.writeAndFlush(object);
    }

    @Override
    public void close() {
        Mercurial.getRuntime().logger().info("Closing connection " + getFullIdentifier() + " on channel " + channel.id().asShortText());
        available = false;

        onlinePlayers.forEach(player -> {
            player.message(" ", "&cDisconnecting you from the server..");
            player.close();
        });
        onlinePlayers.clear();


        write(new Exit01().info("PREVIOUS::SUCCESS")); // Return modified exit signal at previous pipeline

        try {
            Mercurial.getRuntime().removeServer(this);
            Mercurial.getRuntime().logger().info(getFullIdentifier() + ": Connection closed.");

            channel.close().sync();
            channel = null;
        } catch (InterruptedException e) {
            Mercurial.getRuntime().logger().warning("An error occurred whilst closing a channel.");
        } finally {
            channel = null;
        }
    }

    public Connection<String, Player> open(Channel channel) throws NetException {
        Mercurial.getRuntime().logger().info(getFullIdentifier() + " opening on channel " + channel.id().asShortText());
        if (isRunning())
            throw new NetException(getIdentifier() + ": A connection has already been bound to this channel.");

        this.channel = channel;
        this.available = true;
        Mercurial.getRuntime().addServer(this);
        Mercurial.getRuntime().logger().info(getIdentifier() + ": Connection opened.");
        return this;
    }

    public boolean isRunning() {
        return available && channel != null && channel.isOpen();
    }

    public void remove(Player player) {
        if (searchPlayer(player.getIdentifier()).isPresent())
            onlinePlayers.remove(player);
    }

    public Optional<Player> searchPlayer(UUID uuid) {
        for (Player player : onlinePlayers)
            if (player.getIdentifier().toString().equals(uuid.toString()))
                return Optional.of(player);
        return Optional.empty();
    }

    public Channel channel() {
        return channel;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Server lazy() {
        return this;
    }
}
