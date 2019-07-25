package com.judopay.api;

import static java.util.UUID.randomUUID;

/**
 * A Request to be requested with the judo API
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public abstract class Request {
    private final boolean uniqueRequest;
    protected String yourPaymentReference;

    protected Request(final String yourPaymentReference) {
        this(false, yourPaymentReference);
    }

    protected Request() {
        this(true, randomUUID().toString());
    }

    private Request(final boolean uniqueRequest, final String yourPaymentReference) {
        this.uniqueRequest = uniqueRequest;
        this.yourPaymentReference = yourPaymentReference;
    }

    protected static <T> T checkNotNull(final T obj) {
        if (obj == null) {
            throw new ParameterError();
        }
        return obj;
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
    }
}
