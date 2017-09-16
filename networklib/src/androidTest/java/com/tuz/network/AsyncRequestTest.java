package com.tuz.network;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AsyncRequestTest {

    private static final String FAKE_SERVER_ROOT = "https://jsonplaceholder.typicode.com";

    static class Listener<T> implements RequestListener<T> {

        private T mResult;

        private Semaphore mSemaphore;

        private long mProgress;

        private long mTotalSize;

        Listener(Semaphore semaphore) {
            mSemaphore = semaphore;
        }

        @Override
        public void onPreExecution(Request<T> request) {

        }

        @Override
        public void onExecutionProgress(Request<T> request, TransferProgress progress) {
            mProgress = progress.getTransferredBytes();
            mTotalSize = progress.getTotalBytes();
        }

        @Override
        public void onExecutionDone(Request<T> request, T result) {
            mSemaphore.release();
            mResult = result;
        }

        T getResult() {
            return mResult;
        }
    }

    @Test
    public void testGetExecuteAsync() throws Exception {
        RequestBuilder<String> builder =  new RequestBuilder<>(FAKE_SERVER_ROOT + "/posts/1", Method.GET, Parsers.newStringParser());
        Request<String> request = builder.build();

        final Semaphore semaphore = new Semaphore(0);
        Listener<String> listener = new Listener<>(semaphore);
        request.executeAsync(listener);

        semaphore.acquire();
        assertNotNull(listener.getResult());
    }
}
