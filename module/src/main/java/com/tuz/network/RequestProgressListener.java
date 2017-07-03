package com.tuz.network;

/**
 * Created by henrik on 2017-04-21.
 */

public interface RequestProgressListener<Output> {

    /**
     * Callback when transfer progress has been made.
     *
     * @param request the network request.
     * @param progress the current progress.
     */
    void onExecutionProgress(Request<Output> request, TransferProgress progress);
}
