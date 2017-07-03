package com.tuz.network;

/**
 * A network execption.
 */
public class NetworkException extends Exception {

    /**
     * Constructor.
     *
     * @param msg the detailed message.
     */
    public NetworkException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     *
     * @param throwable the cause of this exception.
     */
    public NetworkException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor.
     *
     * @param msg       the detailed message.
     * @param throwable the cause of the exception.
     */
    public NetworkException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
