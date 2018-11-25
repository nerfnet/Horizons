package net.havocmc.transport.entity;

/**
 * Created by Giovanni on 27/02/2018.
 */
public interface ConnectionResult {

    /**
     * Returns whether the connection has successfully been created.
     */
    boolean success();

    /**
     * Completes the connection sequence.
     */
    ConnectionResult complete();
}
