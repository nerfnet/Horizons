package net.havocmc.transport.entity;

import java.io.Closeable;
import java.io.Serializable;

/**
 * Created by Giovanni on 25/02/2018.
 */
public interface CloseableConnection<E extends Serializable, K extends Connection> extends Connection<E, K>, Closeable {

    @Override
    void close();
}
