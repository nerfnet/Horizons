package net.havocmc.transport.proto;

import net.havocmc.transport.pool.Transporter;

import java.io.Serializable;

/**
 * Created by Giovanni on 25/02/2018.
 */
public interface BufferedObject extends Serializable {

    /**
     * Returns the ID of the {@link Transporter#id()} that should handle this object.
     */
    String getTransporterId();
}
