package com.judopay.error;

/**
 * An exception thrown if the Judo credentials have not been initialized correctly.
 */
public class TokenSecretError extends Error {

    public TokenSecretError(final String detailMessage) {
        super(detailMessage);
    }
}
