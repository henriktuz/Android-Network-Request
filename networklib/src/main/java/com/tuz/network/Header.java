package com.tuz.network;

import java.util.Objects;

/**
 * Class representing a header in a network request.
 */
public class Header {

    /**
     * The key.
     */
    private final String mKey;

    /**
     * The value.
     */
    private final String mValue;

    /**
     * Constructor.
     *
     * @param key the key of the header.
     * @param value the value of the header.
     * @throws NullPointerException if either the the value is null.
     */
    public Header(String key, String value) {
        if (key == null && value == null) {
            throw new NullPointerException("Neither key nor value can be null.");
        }
        mKey = key;
        mValue = value;
    }

    /**
     * The key of the header.
     *
     * @return the key.
     */
    public String getKey() {
        return mKey;
    }

    /**
     * The value of the header.
     *
     * @return the value.
     */
    public String getValue() {
        return mValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mKey, mValue);
    }

    @Override
    public boolean equals(Object other) {
        boolean equals = false;
        if (other instanceof Header) {
            Header header = (Header) other;
            equals = mKey.equals(header.mKey) && mValue.equals(header.mValue);
        }
        return equals;
    }
}
