package com.judopay.error;

/**
 * An exception thrown if the Judo credentials have not been initialized correctly.
 */
public class TokenSecretError extends Error {

    public TokenSecretError(String detailMessage) {
        super(detailMessage);
    }
}
