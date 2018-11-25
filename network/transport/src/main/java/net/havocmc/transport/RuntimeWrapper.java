package net.havocmc.transport;

import io.netty.channel.Channel;
import net.havocmc.transport.bootstrap.Bootstrap;
import net.havocmc.transport.bootstrap.BootstrapProperties;
import net.havocmc.transport.bootstrap.ShutdownHook;
import net.havocmc.transport.concurrent.TransportThreadFactory;
import net.havocmc.transport.exception.NetException;
import net.havocmc.transport.exception.NetExceptionHandler;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.pool.TransporterPool;
import net.havocmc.transport.proto.BufferedObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by Giovanni on 25/02/2018.
 */
public abstract class RuntimeWrapper<E extends BootstrapProperties> implements TransporterPool {

    private final String name;
    private final InetSocketAddress socketAddress;
    private final TransportThreadFactory threadFactory;
    private final NetExceptionHandler exceptionHandler;
    private E properties;
    private Logger logger;
    private Thread bootstrapThread;
    private Channel channel;

    public RuntimeWrapper(@Nonnull InetSocketAddress socketAddress, @Nonnull E properties) {
        this.name = properties.read("INSTANCE_NAME", String.class);
        this.socketAddress = socketAddress;
        this.properties = properties;
        this.threadFactory = new TransportThreadFactory(this, 35);
        this.logger = Logger.getLogger("Runtime@" + socketAddress.getPort());
        this.exceptionHandler = new NetExceptionHandler(this);
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(new Handler() {
            @Override public void publish(LogRecord record) {
                if (record.getLevel() == Level.CONFIG) {
                    System.out.println("... | " + record.getMessage());
                    return;
                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder
                        .append(record.getLevel().getName())
                        .append(" | ")
                        .append(record.getMessage());

                if (record.getLevel() == Level.SEVERE) {
                    // Used by the NetExceptionHandler, mainly for debugging.
                    System.err.println("A severe error has occurred which has interrupted the runtime.");
                    System.err.println("See full report below for more information.");
                    System.err.println("Runtime will attempt to continue in 15 seconds.");
                    // Verify if the properties are loaded, existent, and properly read.
                    System.err.println("Verifying mercurial.boot image");
                    properties.printImage(true);
                    // @TODO

                    System.err.println(stringBuilder.toString());

                    try {
                        Thread.sleep(15000L);
                    } catch (InterruptedException e) {
                        System.err.println("Runtime failed to continue, exit.");
                        System.exit(0);
                    }
                    return;
                }

                if (record.getLevel() == Level.WARNING) {
                    System.err.println(stringBuilder.toString());
                    return;
                }

                System.out.println(stringBuilder.toString());
            }

            @Override public void flush() {
                System.err.flush();
                System.out.flush();

            }

            @Override public void close() throws SecurityException {
                System.out.println("Log pipeline at " + System.currentTimeMillis());
            }
        });

        this.logger.info("Log pipeline open at " + System.currentTimeMillis());
    }

    /**
     * Starts the runtime wrapper if the {@link Channel} is closed, dead, or null.
     *
     * @param bootstrap The bootstrap function to consume.
     */
    public void bootstrap(Bootstrap bootstrap) throws NetException {
        if (properties == null)
            throw new NetException("Bootstrap failed, properties are null.");

        if (runtimeAvailable())
            throw new NetException("Runtime is already available, bootstrap denied.");

        logger.info(".boot image:");
        properties.printImage(false);

        // Netty will run on a thread assigned just for bootstrap so it won't block the runtime, Mercurial/MercurialRuntime.class.
        bootstrapThread = threadFactory.newThread(() -> {
            logger.info("Performing bootstrap on thread " + bootstrapThread.getName() + " @ " + socketAddress.getHostString() + ":" + socketAddress.getPort());
            bootstrap.consume();
        });
        bootstrapThread.start();
    }

    /**
     * Stops the Runtime Wrapper and closes the connection.
     */
    public void exit(@Nullable ShutdownHook shutdownHook) throws NetException {
        if (!runtimeAvailable())
            throw new NetException("Exit failure: Runtime is not alive.");

        logger.info("Mercurial exit requested, please wait..");

        // EDIT 02/04/2018:
        // Should not be done asynchronous since the shutdown hook has to be executed after the channel has been closed.
        try {
            channel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Let the JVM GC the bootstrap thread because some additional tasks might still be running
        threadFactory.close();

        logger.info("Connection closed.");
        logger.info("Execute ShutdownHook..");
        logger = null;

        if (shutdownHook != null)
            if (shutdownHook.isNewThread()) {
                Thread inversionThread = threadFactory.newThread(() -> {
                    logger.info("ShutdownHook requested on new thread..");
                    shutdownHook.consume();
                });
                inversionThread.start();
            } else shutdownHook.consume();
    }


    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public E getProperties() {
        return properties;
    }

    public NetExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public boolean runtimeAvailable() {
        return channel != null && channel.isOpen();
    }

    public String getInstanceName() {
        return name;
    }

    public Channel channel() {
        return channel;
    }

    public TransportThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public RuntimeWrapper updateChannel(Channel channel) {
        if (runtimeAvailable()) return this;
        this.channel = channel;
        return this;
    }

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public boolean canHandle(BufferedObject object) {
        for (Transporter transporter : poolArray()) {
            if (transporter.id().equals(object.getTransporterId()))
                return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("all")
    public void handleFirst(BufferedObject object, Channel fromChannel) {
        for (Transporter transporter : poolArray()) {
            if (transporter.id().equals(object.getTransporterId())) {
                transporter.inWrite(object, fromChannel);
                return;
            }
        }
        logger.warning("Failed to handle BufferedObject#" + object.toString() + " but still attempted to handle, no #canHandle?");
    }

    @Override
    @SuppressWarnings("all")
    public void handleAll(BufferedObject object, Channel fromChannel) {
        for (Transporter transporter : poolArray()) {
            if (transporter.id().equals(object.getTransporterId()))
                transporter.inWrite(object, fromChannel);
        }
    }
}
