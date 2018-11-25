package net.havocmc.transport.pool;

import io.netty.channel.Channel;
import net.havocmc.transport.RuntimeWrapper;
import net.havocmc.transport.proto.BufferedObject;

/**
 * Created by Giovanni on 25/02/2018.
 */
public interface Transporter<E extends BufferedObject, R extends RuntimeWrapper> {

    /**
     * Writes a {@link BufferedObject} to this transporter, which then handles it.
     */
    void inWrite(E object, Channel channel);

    /**
     * Returns the ID of this transporter.
     */
    String id();

    /**
     * Returns the {@link RuntimeWrapper}.
     */
    R getRuntime();
}
