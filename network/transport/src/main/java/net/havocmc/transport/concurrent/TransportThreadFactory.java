package net.havocmc.transport.concurrent;

import net.havocmc.transport.RuntimeWrapper;

import javax.annotation.Nonnull;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Giovanni on 25/02/2018.
 */
public class TransportThreadFactory implements ThreadFactory {

    // TODO Let databases use this thread factory.
    private final RuntimeWrapper wrapper;
    private final AtomicInteger craftedThreads = new AtomicInteger(0);
    private final int maxThreads;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(25);

    public TransportThreadFactory(RuntimeWrapper wrapper, int maxThreads) {
        this.wrapper = wrapper;
        this.maxThreads = maxThreads;
    }

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        if (craftedThreads.get() >= maxThreads) {
            wrapper.logger().warning("TransportThreadFactory failed to supply a thread.");
            return null;
        }

        Thread thread = new Thread(r);
        thread.setName("TransportThread_$" + craftedThreads.incrementAndGet());
        thread.setUncaughtExceptionHandler(wrapper.getExceptionHandler());
        return thread;
    }

    public void close() {
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow().forEach(runnable -> {
                    wrapper.logger().info("Runtime closed task " + runnable.toString());
                });

                if (!executorService.awaitTermination(15, TimeUnit.SECONDS))
                    wrapper.logger().severe("Failed to terminate the TransportThreadFactory.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        wrapper.logger().info("TransportThreadFactory closed gracefully.");
    }

    public AtomicInteger getCraftedThreads() {
        return craftedThreads;
    }

    public ScheduledFuture<?> execute(Runnable runnable, long initialDelay, long interval, TimeUnit timeUnit) {
        return executorService.scheduleAtFixedRate(runnable, initialDelay, interval, timeUnit);
    }

    public ScheduledFuture<?> execute(Runnable runnable, long after, TimeUnit unit) {
        return executorService.scheduleWithFixedDelay(runnable, after, after, unit);
    }

    public void executeVoid(Runnable runnable) {
        executorService.execute(runnable);
    }
}
