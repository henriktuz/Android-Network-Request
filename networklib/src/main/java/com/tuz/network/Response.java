package com.tuz.network;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a http response.
 */
final class Response implements Closeable {

    /**
     * The http status.
     */
    private int mStatus;

    /**
     * The input stream.
     */
    private InputStream mInputStream;

    /**
     * The mime type.
     */
    private String mMime;

    /**
     * The set of headers.
     */
    private Set<Header> mHeaders;

    /**
     * The http connection.
     */
    private HttpURLConnection mConnection;

    /**
     * Constructor.
     *
     * @param connection the http connection.
     * @throws IOException if connection is interrupted while reading.
     */
    Response(HttpURLConnection connection) throws IOException {
        mConnection = connection;
        mMime = mConnection.getContentType();
        mStatus = mConnection.getResponseCode();
        Map<String, List<String>> headerFields = mConnection.getHeaderFields();
        mHeaders = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            for (String value : entry.getValue()) {
                mHeaders.add(new Header(entry.getKey(), value));
            }
        }

        if (Http.isFailedStatus(mStatus)) {
            mInputStream = mConnection.getErrorStream();
        } else {
            mInputStream = mConnection.getInputStream();
        }
    }

    /**
     * Get the response headers.
     *
     * @return the headers.
     */
    public Set<Header> getHeaders() {
        return mHeaders;
    }

    /**
     * Get the status code.
     *
     * @return the status code.
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * Get the content.
     *
     * @return an input stream.
     */
    public InputStream getInputStream() {
        return mInputStream;
    }

    /**
     * Get the mime type.
     *
     * @return the mime type.
     */
    public String getMime() {
        return mMime;
    }

    @Override
    public void close() throws IOException {
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }
}
