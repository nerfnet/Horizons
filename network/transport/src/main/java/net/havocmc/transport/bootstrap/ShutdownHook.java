package net.havocmc.transport.bootstrap;

import net.havocmc.transport.RuntimeWrapper;

/**
 * Created by Giovanni on 25/02/2018.
 */
public abstract class ShutdownHook extends Bootstrap {

    private final boolean newThread;

    public ShutdownHook(RuntimeWrapper wrapper, boolean newThread) {
        super(wrapper);
        this.newThread = newThread;
    }

    public boolean isNewThread() {
        return newThread;
    }
}
