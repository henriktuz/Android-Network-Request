package com.tuz.network;

/**
 * Listener interface for a request.
 *
 * @param <Output> the output type.
 */
public interface RequestListener<Output> extends RequestProgressListener<Output> {

    /**
     * Callback before the execution starts.
     *
     * @param request the request.
     */
    void onPreExecution(Request<Output> request);

    /**
     * Callback when the execution is done.
     *
     * @param request the request.
     * @param result  the result.
     */
    void onExecutionDone(Request<Output> request, Output result);
}
