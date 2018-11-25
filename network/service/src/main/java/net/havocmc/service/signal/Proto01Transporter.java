package net.havocmc.service.signal;

import io.netty.channel.Channel;
import net.havocmc.service.MercurialRuntime;
import net.havocmc.service.server.Server;
import net.havocmc.transport.exception.NetException;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.connection.Bootstrap01;
import net.havocmc.transport.proto.signal.connection.Exit01;

import java.util.Optional;

/**
 * Created by Giovanni on 26/02/2018.
 */
public class Proto01Transporter implements Transporter<BufferedObject, MercurialRuntime> {

    private final MercurialRuntime runtime;

    public Proto01Transporter(MercurialRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void inWrite(BufferedObject object, Channel channel) {
        if (object instanceof Exit01) {
            Exit01 exit01 = (Exit01) object;
            runtime.logger().info("Received exit signal from " + exit01.getHostString() + " ..");
            String data = exit01.getConnectionData();

            String serverName = data.split("::")[1];

            if (!runtime.serverConnected(serverName)) {
                runtime.logger().warning("Runtime could not find server with name '" + serverName + "'");
                channel.writeAndFlush(exit01.info(exit01.getConnectionData() + "::" + "ERROR"));
                return;
            }

            Optional<Server> serverOptional = runtime.findServer(serverName);
            if (!serverOptional.isPresent()) {
                channel.writeAndFlush(exit01.info(exit01.getConnectionData() + "::" + "ERROR"));
                return;
            }

            Server server = serverOptional.get();
            server.close();
        }

        if (object instanceof Bootstrap01) {
            Bootstrap01 bootstrap01 = (Bootstrap01) object;
            runtime.logger().info("Received bootstrap signal from " + bootstrap01.getHostString() + " ..");

            // BOOTSTRAP::server_name
            String data = bootstrap01.getData();
            if (!data.toLowerCase().startsWith("bootstrap")) {
                runtime.logger().warning("Invalid bootstrap signal from " + bootstrap01.getHostString());
                return;
            }

            String serverName = data.split("::")[1];
            String debugName = "@" + bootstrap01.getHostString();

            if (runtime.serverConnected(serverName)) {
                runtime.logger().warning("Server" + debugName + " bootstrap request declined.");
                return;
            }

            Server server = new Server(serverName, bootstrap01.getProperties());
            try {
                server.open(channel);
            } catch (NetException e) {
                runtime.logger().warning("Server" + debugName + " bootstrap failed.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public String id() {
        return "0x01";
    }

    @Override
    public MercurialRuntime getRuntime() {
        return runtime;
    }
}
