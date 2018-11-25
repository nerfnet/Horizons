package net.havocmc.transport.entity;

import io.netty.channel.Channel;
import net.havocmc.transport.proto.BufferedObject;

import java.io.Serializable;

/**
 * Created by Giovanni on 25/02/2018.
 */
public interface Connection<E extends Serializable, K extends Connection> extends Serializable {

    /**
     * Returns the name of this connection.
     */
    String getName();

    /**
     * Returns the unique identifier of this connection.
     */
    E getIdentifier();

    /**
     * Returns a readable mix of {@link #getName()} and {@link #getIdentifier()} as a String.
     */
    String getFullIdentifier();

    /**
     * Attempts to read a {@link Connection}'s data and load it.
     */
    ConnectionResult attemptRead(K connection, Channel previousChannel);

    /**
     * Rejects the connection.
     */
    void reject(String context, Channel writeTo);

    /**
     * Writes a {@link BufferedObject} to the connection's pipeline.
     */
    void write(BufferedObject object);
}
