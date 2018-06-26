package com.tuz.network;

/**
 * Interface returned by an async operation.
 */
public interface Canceller {

    /**
     * Cancels an ongoing task.
     */
    void cancel();
}
