package com.tuz.network;

/**
 * The interface for executing requests.
 *
 * @param <Output> the output of the request.
 * @see RequestBuilder
 */
public interface Request<Output> {

    /**
     * Execute the request.
     *
     * @param listener the progress listener.
     * @return the output.
     * @throws NetworkException when the request fails.
     */
    Output execute(RequestProgressListener<Output> listener) throws NetworkException;

    /**
     * Execute the request.
     *
     * @return the output.
     */
    Output execute() throws NetworkException;

    /**
     * Execute the request.
     *
     * @param retries the number of retries.
     * @return the output.
     */
    Output execute(int retries);

    /**
     * Execute the request asynchronously.
     *
     * @return a canceller.
     */
    Canceller executeAsync(RequestListener<Output> listener);
}
