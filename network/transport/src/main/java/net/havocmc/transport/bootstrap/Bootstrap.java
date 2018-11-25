package net.havocmc.transport.bootstrap;

import net.havocmc.transport.RuntimeWrapper;

/**
 * Created by Giovanni on 25/02/2018.
 */
public abstract class Bootstrap {

    private final RuntimeWrapper wrapper;

    public Bootstrap(RuntimeWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public abstract void consume();
}
