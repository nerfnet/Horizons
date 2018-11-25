package net.havocmc.service.signal;

import io.netty.channel.Channel;
import net.havocmc.service.MercurialRuntime;
import net.havocmc.service.player.Player;
import net.havocmc.service.player.PlayerContainer;
import net.havocmc.service.server.Server;
import net.havocmc.transport.entity.ConnectionResult;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.player.Connect02;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Giovanni on 26/02/2018.
 */
public class Proto02Transporter implements Transporter<BufferedObject, MercurialRuntime> {

    private final MercurialRuntime runtime;

    public Proto02Transporter(MercurialRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void inWrite(BufferedObject object, Channel channel) {
        if (object instanceof Connect02) {
            Connect02 connect02 = (Connect02) object;
            UUID uniqueId = connect02.getUniqueId();
            String connectionData = connect02.getData();

            //SYNC::connect@0xORIGIN
            String[] data = connectionData.split("::")[1].split("@");

            if (data[0].equalsIgnoreCase("connect")) {
                String origin = data[1];
                String playerName = data[2];

                if (!runtime.serverConnected(origin))
                    return;

                Optional<Server> serverQuery = runtime.findServer(origin);
                if (!serverQuery.isPresent())
                    return;

                Optional<Player> playerQuery = runtime.findPlayer(uniqueId);
                if (!playerQuery.isPresent()) {
                    runtime.database().findPlayer(uniqueId, player -> {
                        Player playerConnection = player.orElseGet(() -> new Player(playerName, uniqueId, null));

                        if (playerConnection.container() == null)
                            playerConnection.setContainer(new PlayerContainer());

                        Server server = serverQuery.get();
                        runtime.addPlayer(playerConnection);

                        ConnectionResult result = playerConnection.attemptRead(server, channel).complete();
                        if (result.success()) {
                            runtime.logger().info("Player@" + playerConnection.getFullIdentifier() + " connected to " + server.getFullIdentifier());
                        } else {
                            runtime.database().updatePlayer(playerConnection, future -> {
                                runtime.database().updateIsland(future.container().getIsland(), null);
                                runtime.removePlayer(future);
                            });
                        }
                    });
                    return;
                }

                Player player = playerQuery.get();
                Server server = serverQuery.get();

                if (player.onServer(server)) return;

                ConnectionResult result = player.attemptRead(server, channel).complete();
                if (result.success()) {
                    runtime.logger().info("Player@" + player.getFullIdentifier() + " connected to " + server.getFullIdentifier());
                } else {
                    runtime.database().updatePlayer(player, future -> {
                        runtime.database().updateIsland(future.container().getIsland(), null);
                    });
                }
            }
        }
    }

    @Override
    public String id() {
        return "0x02";
    }

    @Override
    public MercurialRuntime getRuntime() {
        return runtime;
    }
}
