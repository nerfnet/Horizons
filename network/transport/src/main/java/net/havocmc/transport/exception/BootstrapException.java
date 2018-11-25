package net.havocmc.transport.exception;

/**
 * Created by Giovanni on 25/02/2018.
 */
public class BootstrapException extends NetException {

    public BootstrapException(String cause, long time) {
        super("Bootstrap error at " + time + ": " + cause);
    }
}
