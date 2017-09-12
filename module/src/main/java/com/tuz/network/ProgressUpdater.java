package com.tuz.network;

/**
 * Interface for updating progress.
 */
interface ProgressUpdater {

    /**
     * Update the progress.
     *
     * @param total   the total size.
     * @param current the current size.
     */
    void update(long total, long current);
}
