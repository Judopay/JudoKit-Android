package com.judopay.exception;

/**
 * An exception thrown if the Judo credentials have not been initialized correctly.
 */
public class JudoInitializationException extends RuntimeException {

    public JudoInitializationException(String detailMessage) {
        super(detailMessage);
    }
}
