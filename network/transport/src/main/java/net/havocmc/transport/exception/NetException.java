package net.havocmc.transport.exception;

import javax.annotation.Nonnull;

/**
 * Created by Giovanni on 25/02/2018.
 */
public class NetException extends Exception {

    public NetException(@Nonnull String message) {
        super(message);
    }

    public NetException(@Nonnull Exception e) {
        super(e);
    }

    public NetException(@Nonnull String message, Exception e) {
        super(message, e);
    }
}
