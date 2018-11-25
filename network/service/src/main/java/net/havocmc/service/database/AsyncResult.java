package net.havocmc.service.database;

/**
 * Created by Giovanni on 27/02/2018.
 */
public interface AsyncResult {

    /**
     * Returns whether the task has been completed.
     */
    boolean complete();

    /**
     * Returns the past {@link Runnable} of this result, representing the task that was ran in the past.
     */
    Runnable getPast();

    void end();
}
