package net.havocmc.transport.pool;

import io.netty.channel.Channel;
import net.havocmc.transport.RuntimeWrapper;
import net.havocmc.transport.proto.BufferedObject;

import java.util.logging.Logger;

/**
 * Created by Giovanni on 25/02/2018.
 */
public interface TransporterPool {

    /**
     * Returns a mutable array of {@link Transporter}s, respecting their generic types.
     */
    Transporter<?, ?>[] poolArray();

    /**
     * Returns the objects that are queued for transportation.
     */
    BufferedObject[] objectsInQueue();

    /**
     * Returns whether there is a {@link Transporter} in the pool that can handle the given object.
     */
    boolean canHandle(BufferedObject object);

    /**
     * Lets the first {@link Transporter} in the pool that can handle the object handle it.
     *
     * @param fromChannel Origin channel.
     */
    void handleFirst(BufferedObject object, Channel fromChannel);

    /**
     * Lets all {@link Transporter}s that can handle this object handle it.
     */
    void handleAll(BufferedObject object, Channel fromChannel);

    /**
     * Returns the {@link RuntimeWrapper#logger()}
     */
    Logger logger();
}
