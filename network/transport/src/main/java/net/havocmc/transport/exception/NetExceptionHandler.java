package net.havocmc.transport.exception;

import net.havocmc.transport.RuntimeWrapper;

/**
 * Created by Giovanni on 25/02/2018.
 */
public class NetExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final RuntimeWrapper wrapper;

    public NetExceptionHandler(RuntimeWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        wrapper.logger().severe("Exception occurred in thread " + t.getName());
        e.printStackTrace();
    }
}
