package com.tuz.network;

/**
 * Implementation of the Request interface.
 */

class NetworkRequest<Output> implements Request<Output>, HttpRequest.IHttpTransferListener {

    /**
     * The http request.
     */
    private final HttpRequest mRequest;

    /**
     * The response parser.
     */
    private final ResponseParser<Output> mParser;

    /**
     * The request progress listener.
     */
    private RequestProgressListener<Output> mProgressListener;

    /**
     * Constructor.
     *
     * @param request the http request.
     * @param parser  the response parser.
     */
    NetworkRequest(HttpRequest request, ResponseParser<Output> parser) {
        mRequest = request;
        mParser = parser;
    }

    @Override
    public Output execute(RequestProgressListener<Output> listener) throws NetworkException {
        mProgressListener = listener;
        return execute();
    }

    @Override
    public Output execute() throws NetworkException {
        mRequest.setProgressListener(this);
        Output output = null;

        try (Response response = mRequest.execute()) {
            int status = response.getStatus();
            if (Http.isSuccessfulStatus(status)) {
               output = mParser.parseResponse(response);
            }
            return output;
        } catch (Exception e) {
            throw new NetworkException(e);
        }
    }

    @Override
    public Output execute(final int retries) {
        Output output = null;
        int retry = 0;
        try {
            do {

                // TODO: Backoff time
                output = execute();
            } while (output != null && retry++ < retries);
        } catch (NetworkException e) {
            // Nop
        } finally {
            return output;
        }
    }

    @Override
    public Canceller executeAsync(RequestListener<Output> listener) {
        NetworkTask<Output> task = new NetworkTask<>(this, listener);
        task.execute();
        return task;
    }


    @Override
    public void onTransferProgressUpdated(TransferProgress progress) {
        if (mProgressListener != null) {
            mProgressListener.onExecutionProgress(this, progress);
        }
    }
}
