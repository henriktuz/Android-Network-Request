package com.tuz.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by henrik on 2017-07-03.
 */

public class MultipartFormBody {

    /**
     * Multipart boundary identifier.
     */
    private static final String BOUNDARY = "*****";

    /**
     * Line end identifier.
     */
    private static final String LINE_END = "\r\n";

    /**
     * Two hyphens identifier.
     */
    private static final String TWO_HYPHENS = "--";

    /**
     * The text fields of the form.
     */
    private final Map<String, String> mTextFields;

    public MultipartFormBody() {
        mTextFields = new HashMap<>();
    }

    public void addTextField(String key, String value) {
        mTextFields.put(key, value);
    }

    private static String getMultipartBoundary() {
        return BOUNDARY + System.currentTimeMillis() + BOUNDARY;
    }

    /**
     * Get the multipart content type.
     *
     * @param boundary the boundary string.
     * @return the multipart boundary string.
     */
    public static String getMultipartContentType(String boundary) {
        return "multipart/form-data; boundary=" + boundary;

    }
}
