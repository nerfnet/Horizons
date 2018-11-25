package net.havocmc.horizons;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.havocmc.horizons.game.api.mechanic.MechanicFactory;
import net.havocmc.transport.NettyObjectTransportAdapter;
import net.havocmc.transport.bootstrap.Bootstrap;
import net.havocmc.transport.bootstrap.BootstrapProperties;
import net.havocmc.transport.bootstrap.ShutdownHook;
import net.havocmc.transport.concurrent.TransportThreadFactory;
import net.havocmc.transport.exception.NetException;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.connection.Bootstrap01;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Created by Giovanni on 03/03/2018.
 */
public class Horizons extends JavaPlugin {

    private static Horizons instance;

    private static HorizonsRuntime runtime;
    private static MechanicFactory mechanicFactory;

    public static Horizons getInstance() {
        return instance;
    }

    public static HorizonsRuntime runtime() {
        return runtime;
    }

    public static MechanicFactory getMechanicFactory() {
        return mechanicFactory;
    }

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getLogger().info("Reading mercurial.boot image ..");

        // IO operations
        File bootDir = new File("mercurial");
        if (!bootDir.exists()) bootDir.mkdir();

        File bootFile = new File("mercurial", "mercurial.boot");
        System.out.println(getResource("mercurial.boot") == null);

        try {
            if (!bootFile.exists())
                Files.copy(getResource("mercurial.boot"), bootFile.toPath());
        } catch (IOException e) {
            Bukkit.getLogger().severe(e.getMessage());
            Bukkit.shutdown();
        }

        Optional<BootstrapProperties> properties = BootstrapProperties.load(bootFile);

        if (!properties.isPresent()) {
            Bukkit.getLogger().severe("mercurial.boot image not present.");
            Bukkit.shutdown();
            return;
        }

        // Bootstrap
        BootstrapProperties bootstrapProperties = properties.get();
        String address = bootstrapProperties.read("AF_INET_ADDRESS", String.class);
        Double port = bootstrapProperties.read("AF_INET_PORT", Double.class);

        InetSocketAddress socketAddress = new InetSocketAddress(address, port.intValue());
        runtime = new HorizonsRuntime(socketAddress, bootstrapProperties);

        String mercurialAddress = bootstrapProperties.read("MERCURIAL_ADDRESS", String.class);
        Double mercurialPort = bootstrapProperties.read("MERCURIAL_PORT", Double.class);

        InetSocketAddress mercurialSocket = new InetSocketAddress(mercurialAddress, mercurialPort.intValue());

        try {
            runtime.bootstrap(new Bootstrap(runtime) {
                @Override public void consume() {
                    io.netty.bootstrap.Bootstrap bootstrap = new io.netty.bootstrap.Bootstrap();
                    try {
                        runtime.updateChannel(bootstrap
                                .channel(NioSocketChannel.class)
                                .group(new NioEventLoopGroup())
                                .handler(new ChannelInitializer<SocketChannel>() {
                                    @Override protected void initChannel(SocketChannel socketChannel) throws Exception {
                                        ChannelPipeline pipeline = socketChannel.pipeline();
                                        pipeline.addLast(new ObjectEncoder());
                                        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(BufferedObject.class.getClassLoader())));
                                        pipeline.addLast(new NettyObjectTransportAdapter(runtime));
                                    }
                                }).remoteAddress(mercurialSocket).connect().sync().channel());

                        Bootstrap01 bootstrap01 = new Bootstrap01()
                                .connectionData("BOOTSTRAP::" + runtime.getInstanceName())
                                .server(socketAddress.getHostString() + ":" + socketAddress.getPort())
                                .properties(bootstrapProperties);

                        runtime.channel().writeAndFlush(bootstrap01);
                        runtime.logger().info("Horizons is now connected to Mercurial on " + socketAddress.getHostString() + ":" + socketAddress.getPort());
                    } catch (InterruptedException e) {
                        Bukkit.getLogger().severe(e.getMessage());
                        Bukkit.shutdown();
                    }
                }
            });
        } catch (NetException e) {
            Bukkit.getLogger().severe(e.getMessage());

        }

        mechanicFactory = new MechanicFactory(this).bootstrap();
    }

    @Override
    public void onDisable() {
        Horizons.runtime().logger().warning("Abnormal exit via #onDisable!");
    }

    public void close() {
        // Force on CraftBukkit main thread
        Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), () -> {
            try {
                runtime.exit(new ShutdownHook(runtime, false) {
                    @Override public void consume() {
                        Bukkit.shutdown();
                    }
                });
            } catch (NetException e) {
                Bukkit.shutdown();
            }
        });
    }

    public TransportThreadFactory getThreadFactory() {
        return runtime().getThreadFactory();
    }

    public String getHostString() {
        return runtime.getSocketAddress().getHostString() + ":" + runtime().getSocketAddress().getPort();
    }
}
