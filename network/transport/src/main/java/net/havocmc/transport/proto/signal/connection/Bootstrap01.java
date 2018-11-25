package net.havocmc.transport.proto.signal.connection;

import net.havocmc.transport.bootstrap.BootstrapProperties;
import net.havocmc.transport.proto.BufferedObject;

import javax.annotation.Nonnull;

/**
 * Created by Giovanni on 26/02/2018.
 */
public class Bootstrap01 implements BufferedObject {

    private String hostString;
    private BootstrapProperties properties;
    private String connectionData;

    public Bootstrap01 connectionData(@Nonnull String connectionData) {
        this.connectionData = connectionData;
        return this;
    }

    public Bootstrap01 server(@Nonnull String hostString) {
        this.hostString = hostString;
        return this;
    }

    public Bootstrap01 properties(@Nonnull BootstrapProperties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x01";
    }

    public String getHostString() {
        return hostString;
    }

    public BootstrapProperties getProperties() {
        return properties;
    }

    public String getData() {
        return connectionData;
    }
}
