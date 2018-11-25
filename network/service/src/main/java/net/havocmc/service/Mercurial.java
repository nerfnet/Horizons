package net.havocmc.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.havocmc.transport.NettyObjectTransportAdapter;
import net.havocmc.transport.bootstrap.Bootstrap;
import net.havocmc.transport.bootstrap.BootstrapProperties;
import net.havocmc.transport.exception.NetException;
import net.havocmc.transport.proto.BufferedObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Created by Giovanni on 25/02/2018.
 * <p>
 * HavocMC's Horizons cross-network communication implementation.
 */
public class Mercurial {

    private static MercurialRuntime runtime;

    public static void main(String[] args) throws NetException {
        System.out.println("Reading mercurial.boot image ..");
        File bootFile = new File("mercurial.boot");

        // Cancel when exception occurs.
        try {
            if (!bootFile.exists())
                Files.copy(Mercurial.class.getResourceAsStream("/mercurial.boot"), bootFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        Optional<BootstrapProperties> properties = BootstrapProperties.load(bootFile);

        if (!properties.isPresent()) {
            System.err.println("mercurial.boot image not present.");
            System.exit(0);

            return;
        }

        BootstrapProperties bootstrapProperties = properties.get();
        String address = bootstrapProperties.read("AF_INET_ADDRESS", String.class);
        Double port = bootstrapProperties.read("AF_INET_PORT", Double.class);

        InetSocketAddress socketAddress = new InetSocketAddress(address, port.intValue());
        runtime = new MercurialRuntime(socketAddress, bootstrapProperties);

        runtime.bootstrap(new Bootstrap(runtime) {
            @Override public void consume() {
                ServerBootstrap bootstrap = new ServerBootstrap();
                try {
                    runtime.updateChannel(bootstrap
                            .channel(NioServerSocketChannel.class)
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    pipeline.addLast(new ObjectEncoder());
                                    pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(BufferedObject.class.getClassLoader())));
                                    // Hierarchy, BufferedObject pipeline last
                                    pipeline.addLast(new NettyObjectTransportAdapter(runtime));
                                }
                            })
                            .bind(address, socketAddress.getPort())
                            .sync().channel());
                    runtime.logger().info("Mercurial is now running on " + socketAddress.getHostString() + ":" + socketAddress.getPort());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        });

        runtime.databaseBootstrap();
    }

    public static MercurialRuntime getRuntime() {
        return runtime;
    }

    /**
     * Lazy return.
     */
    public static MercurialRuntime runtime() {
        return runtime;
    }
}
