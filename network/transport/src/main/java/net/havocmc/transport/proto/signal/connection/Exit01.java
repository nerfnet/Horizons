package net.havocmc.transport.proto.signal.connection;

import net.havocmc.transport.proto.BufferedObject;

/**
 * Created by Giovanni on 25/03/2018.
 */
public class Exit01 implements BufferedObject {

    private String hostString;
    private String connectionData;

    public Exit01 info(String data) {
        this.connectionData = data;
        return this;
    }

    public Exit01 server(String hostString) {
        this.hostString = hostString;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x01";
    }

    public String getHostString() {
        return hostString;
    }

    public String getConnectionData() {
        return connectionData;
    }
}
