package com.tuz.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.security.cert.Certificate;

/**
 * Builder for creating a request.
 * <p>
 * <pre>
 * <code>
 *     RequestParser parser = Parsers.newJsonParser();
 *     RequestBuilder builder = new RequestBuilder(Method.GET, url, parser);
 *     builder.addHeader("Content-Type", "application/json");
 *     Request request = builder.build();
 *     JSONObject json = request.execute();
 * </code>
 * </pre>
 *
 * @see Request
 */
public final class RequestBuilder<Output> {

    /**
     * The http request.
     */
    private final HttpRequest mRequest;

    /**
     * The response parser.
     */
    private final ResponseParser<Output> mParser;

    /**
     * Constructor.
     *
     * @param url    the url.
     * @param method the method.
     * @param parser the response parser.
     */
    public RequestBuilder(String url, Method method, ResponseParser<Output> parser) {
        mRequest = new HttpRequest(url, method);
        mParser = parser;
    }

    /**
     * Set if the cached should be used. The cache need to be installed in order for this to be
     * used.
     *
     * @param useCache flag for if the cache should be used, default is false.
     * @return the builder instance.
     * @see Setup#installCache(File, long)
     */
    public RequestBuilder setUseCache(boolean useCache) {
        mRequest.setUseCache(useCache);
        return this;
    }

    /**
     * Add a header to the request.
     *
     * @param key   the key of the header.
     * @param value the value of the header.
     * @return the builder instance.
     * @throws IllegalArgumentException if the value is not either string, long or integer.
     */
    public RequestBuilder addHeader(String key, Object value) {
        mRequest.addHeader(key, value);
        return this;
    }

    /**
     * Add a header to the request.
     *
     * @param header the header.
     * @return the builder instance.
     */
    public RequestBuilder addHeader(Header header) {
        mRequest.addHeader(header);
        return this;
    }

    /**
     * Set a body.
     *
     * @param body     the body.
     * @param mime     the mime type of the body.
     * @param compress flag for if the request should be compressed using gzip.
     * @param stream   flag for if streaming mode should be used.
     * @return the builder instance.
     */
    public RequestBuilder setBody(byte[] body, String mime, boolean compress, boolean stream) {
        mRequest.setBody(body, mime, compress, stream);
        return this;
    }

    /**
     * Set json body.
     *
     * @param json     the json object.
     * @param compress flag for if the request should be compressed using gzip.
     * @param stream   flag for if streaming mode should be used.
     * @return the builder instance.
     */
    public RequestBuilder setBody(JSONObject json, boolean compress, boolean stream) {
        mRequest.setBody(json, compress, stream);
        return this;
    }

    /**
     * Set json body.
     *
     * @param json     the json object.
     * @param compress flag for if the request should be compressed using gzip.
     * @param stream   flag for if streaming mode should be used.
     * @return the builder instance.
     */
    public RequestBuilder setBody(JSONArray json, boolean compress, boolean stream) {
        mRequest.setBody(json, compress, stream);
        return this;
    }

    /**
     * Set a multi part form body.
     *
     * @param body the body.
     * @return the builder instance.
     */
    public RequestBuilder setBody(MultipartForm body) {
        mRequest.setMultipartBody(body);
        return this;
    }

    /**
     * Build the request.
     *
     * @return the Request.
     */
    public Request<Output> build() {
        return new NetworkRequest(mRequest, mParser);
    }
}
