package com.tuz.network;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Class for executing a request asynchronously.
 */
class NetworkTask<Output> extends AsyncTask<Void, TransferProgress, Output>
        implements RequestProgressListener<Output>, Canceller {

    /**
     * The request listener.
     */
    private final WeakReference<RequestListener<Output>> mListener;

    /**
     * The request.
     */
    private final Request<Output> mRequest;

    /**
     * Constructor.
     *
     * @param request  the request.
     * @param listener the request listener.
     */
     NetworkTask(Request<Output> request, RequestListener<Output> listener) {
        mRequest = request;
        mListener = new WeakReference<>(listener);
    }

    @Override
    protected void onPreExecute() {
        RequestListener listener = mListener.get();

        if (listener != null) {
            listener.onPreExecution(mRequest);
        }

    }

    @Override
    protected void onPostExecute(Output output) {
        RequestListener<Output> listener = mListener.get();

        if (listener != null) {
            listener.onExecutionDone(mRequest, output);
        }
    }

    @Override
    protected void onCancelled(Output output) {
        onPostExecute(output);
    }

    @Override
    protected void onProgressUpdate(TransferProgress... progress) {
        RequestListener<Output> listener = mListener.get();

        if (listener != null && progress.length > 0) {
            listener.onExecutionProgress(mRequest, progress[0]);
        }
    }

    @Override
    protected Output doInBackground(Void... unused) {
        Output output = null;

        try {
            output = mRequest.execute(this);
        } catch (NetworkException e) {
            // Nop
        }

        return output;
    }

    @Override
    public void onExecutionProgress(Request<Output> request, TransferProgress progress) {
        if (!isCancelled()) {
            publishProgress(progress);
        }
    }

    @Override
    public void cancel() {
        cancel(true);
    }
}
