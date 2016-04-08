package com.judopay.exception;

/**
 * An exception thrown if the Judo credentials have not been initialized correctly.
 */
public class JudoInitializationError extends RuntimeException {

    public JudoInitializationError(String detailMessage) {
        super(detailMessage);
    }
}
