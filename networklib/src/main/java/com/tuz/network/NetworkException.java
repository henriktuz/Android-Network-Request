package com.tuz.network;

/**
 * A network execption.
 */
public class NetworkException extends Exception {

    /**
     * Constructor.
     *
     * @param throwable the cause of this exception.
     */
    public NetworkException(Throwable throwable) {
        super(throwable);
    }
}
