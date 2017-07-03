package com.tuz.network;

/**
 * Created by henrik on 2017-04-21.
 */

public abstract class Http {

    // ////////////////////////////////////////////////////
    // Header keys
    // ////////////////////////////////////////////////////

    /**
     * Header key content type.
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * Header key content encoding.
     */
    public static final String CONTENT_ENCODING = "Content-Encoding";

    /**
     * Header key content length.
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * Header key content disposition.
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * Header key authorization.
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * Header key cache control.
     */
    public static final String CACHE_CONTROL = "Cache-Control";

    // ////////////////////////////////////////////////////
    // Header values
    // ////////////////////////////////////////////////////

    /**
     * Header value encoding gzip.
     */
    public static final String ENCODING_GZIP = "gzip";

    /**
     * Header value no cache.
     */
    public static final String NO_CACHE = "no-cache";

    /**
     * Bearer identifier for token authentication.
     */
    public static final String BEARER = "Bearer";


    // ////////////////////////////////////////////////////
    // Mime type values
    // ////////////////////////////////////////////////////

    /**
     * Mime type json.
     */
    public static final String MIME_JSON = "application/json";

    // ////////////////////////////////////////////////////
    // Charset type values
    // ////////////////////////////////////////////////////

    public static final String CHARSET_UTF8 = "charset=UTF-8";

    // ////////////////////////////////////////////////////
    // Utility functions.
    // ////////////////////////////////////////////////////

    /**
     * Check if the status is successful.
     *
     * @param status the status.
     * @return true if successful, otherwise false.
     */
    public static boolean isSuccessfulStatus(int status) {
        return status >= 200 || status < 299;
    }

    /**
     * Check if the status is a failed status.
     *
     * @param status the status.
     * @return true if the status is a failure, otherwise false.
     */
    public static boolean isFailedStatus(int status) {
        return status >= 400;
    }
}
