package com.tuz.network;

import android.net.http.HttpResponseCache;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Class for making a http request.
 */

class HttpRequest extends Http {

    /**
     * Listener interface for the transfer progress.
     */
    interface IHttpTransferListener {

        /**
         * Callback for when the transfer progress is updated which is whenever the percentage of
         * the transfer has changed.
         */
        void onTransferProgressUpdated(TransferProgress progress);
    }

    /**
     * The max buffer size.
     */
    private static final int MAX_BUFFER_SIZE = 16 * 1024;

    /**
     * The default read timeout.
     */
    private static final int DEFAULT_READ_TIMEOUT = 1500;

    /**
     * The default connection timeout.
     */
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1500;

    /**
     * The ssl protocol.
     */
    private static final String SSL_PROTOCOL = "TLS";

    /**
     * The certificate key.
     */
    private static final String CERTIFICATE_KEY = "ca";

    /**
     * The url of the request.
     */
    private final String mUrl;

    /**
     * The method of the request.
     */
    private final Method mMethod;

    /**
     * The headers of the request.
     */
    private Set<Header> mHeaders;

    /**
     * Flag for if the cache should be used.
     */
    private boolean mUseCache;

    /**
     * The progress listener.
     */
    private IHttpTransferListener mListener;

    /**
     * Flag for if streaming mode should be used.
     */
    private boolean mUseStreamingMode;

    /**
     * Flag for if body content should be compressed.
     */
    private boolean mCompress;

    /**
     * The body.
     */
    private byte[] mBody;

    /**
     * The form body.
     */
    private MultipartFormBody mFormBody;

    /**
     * The certificate for configuring secure connections.
     */
    private Certificate mCertificate;

    /**
     * The transfer progress.
     */
    private TransferProgress mProgress;

    /**
     * Constructor.
     *
     * @param url    the url of the request.
     * @param method the method.
     */
    HttpRequest(String url, Method method) {
        mUrl = url;
        mMethod = method;
        mHeaders = new HashSet<>();
    }

    /**
     * Set if the cache should be used.
     *
     * @param useCache true if the cache should be used, otherwise false.
     */
    void setUseCache(boolean useCache) {
        mUseCache = useCache;
    }

    /**
     * Set use certificate for pinning.
     *
     * @param certificate the certificate.
     */
    void setCertificate(Certificate certificate) {
        mCertificate = certificate;
    }

    /**
     * Set the transfer listener.
     *
     * @param listener the listener.
     */
    void setProgressListener(IHttpTransferListener listener) {
        mListener = listener;
    }

    /**
     * Add a header.
     *
     * @param header the header.
     * @return true if the header was added, otherwise the header was already added.
     */
    boolean addHeader(Header header) {
        return mHeaders.add(header);
    }

    /**
     * Add a header.
     *
     * @param key   the key.
     * @param value the value.
     * @return true if the header was added, otherwise the header was already added.
     * @throws IllegalArgumentException if the supplied value is not an integer, long or a string.
     */
    boolean addHeader(String key, Object value) {
        if (value instanceof Integer || value instanceof Long || value instanceof String) {
            return mHeaders.add(new Header(key, String.valueOf(value)));
        } else {
            throw new IllegalArgumentException("Unsupported value " + value);
        }
    }

    /**
     * Set the body of the request.
     *
     * @param body     the body.
     * @param mime     the mime type of the data.
     * @param compress true if the body should be compressed using gzip.
     * @param stream   true if streaming mode should be used.
     */
    void setBody(byte[] body, String mime, boolean compress, boolean stream) {
        mBody = body;
        addHeader(CONTENT_TYPE, mime);
        mCompress = compress;
        if (mCompress) {
            addHeader(CONTENT_ENCODING, ENCODING_GZIP);
        }
        mUseStreamingMode = stream;
    }

    /**
     * Set the json body.
     *
     * @param json     the json object.
     * @param compress true if the body should be compressed using gzip.
     * @param stream   true if streaming mode should be used.
     */
    void setBody(JSONObject json, boolean compress, boolean stream) {
        setBody(json.toString().getBytes(StandardCharsets.UTF_8), MIME_JSON, compress, stream);
    }

    /**
     * Execute the request.
     *
     * @return the response.
     * @throws Exception if the execution fails while performing network operations.
     */
    final Response execute() throws Exception {
        mProgress = new TransferProgress();
        HttpURLConnection connection = setupConnection();
        Response response = execute(connection);

        if (mUseCache) {
            // Flush the cache
            HttpResponseCache cache = HttpResponseCache.getInstalled();
            if (cache != null) {
                cache.flush();
            }
        }
        return response;
    }

    /**
     * Execute the request.
     *
     * @param connection the connection.
     * @return the response.
     * @throws Exception if anything goes wrong.
     */
    private Response execute(HttpURLConnection connection) throws IOException {
        if (mBody != null) {
            writeBody(connection, mBody);
        } else {
            connection.setDoOutput(false);
        }
        return new Response(connection);
    }

    /**
     * Set up the connection.
     *
     * @return the http connection.
     * @throws Exception If something went wrong when connection to
     *                   server was made.
     */
    private HttpURLConnection setupConnection() throws Exception {
        URL url = new URL(mUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (connection instanceof HttpsURLConnection && mCertificate != null) {
            configureSecureConnection((HttpsURLConnection) connection);
        }

        if (!mUseCache) {
            // Make sure we are not using the cache
            connection.addRequestProperty(CACHE_CONTROL, NO_CACHE);
        }

        for (Header header : mHeaders) {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }
        connection.setDefaultUseCaches(mUseCache);
        connection.setUseCaches(mUseCache);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        connection.setRequestMethod(mMethod.toString());
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    /**
     * Configure a secure connection.
     *
     * @param connection the network connection.
     * @throws Exception if the configuration fails.
     */
    private void configureSecureConnection(HttpsURLConnection connection) throws Exception {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry(CERTIFICATE_KEY, mCertificate);

        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        // Set the ssl socket factory.
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
    }

    /**
     * Write the body to the connection output.
     *
     * @param connection the connection.
     * @param body       the body.
     * @throws IOException if the writing of the body fails.
     */
    private void writeBody(HttpURLConnection connection, byte[] body) throws IOException {
        int totalBytesTransferred = 0;

        if (mUseStreamingMode) {
            if (mCompress) {
                connection.setChunkedStreamingMode(-1);
            } else {
                connection.setFixedLengthStreamingMode(body.length);
            }
        }

        try (OutputStream os = getOutputStream(connection)) {
            try (InputStream is = new ByteArrayInputStream(body)) {
                byte[] buffer = new byte[MAX_BUFFER_SIZE];
                int read;

                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                    totalBytesTransferred += read;

                    reportNetworkProgress(body.length, totalBytesTransferred);
                }
            }
        }
    }

    /**
     * Get the output stream.
     *
     * @param connection the url connection.
     * @return the output stream.
     * @throws IOException if the outputstream could not be created.
     */
    private OutputStream getOutputStream(HttpURLConnection connection) throws IOException {
        OutputStream os = connection.getOutputStream();
        return mCompress ? new GZIPOutputStream(os) : new DataOutputStream(os);
    }

    /**
     * Report the network progress.
     *
     * @param totalBytes       the total bytes to transfer.
     * @param transferredBytes the bytes transferred so far.
     */
    private void reportNetworkProgress(long totalBytes, long transferredBytes) {
        if (mProgress.update(totalBytes, transferredBytes) && mListener != null) {
            mListener.onTransferProgressUpdated(mProgress);
        }
    }
}
