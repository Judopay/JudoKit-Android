package com.judopay.api;

import static java.util.UUID.randomUUID;

/**
 * A Request to be requested with the judo API
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public abstract class Request {

    private final boolean uniqueRequest;
    protected String yourPaymentReference;

    protected Request(String yourPaymentReference) {
        this(false, yourPaymentReference);
    }

    protected Request() {
        this(true, randomUUID().toString());
    }

    private Request(boolean uniqueRequest, String yourPaymentReference) {
        this.uniqueRequest = uniqueRequest;
        this.yourPaymentReference = yourPaymentReference;
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
    }

    protected static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new ParameterError();
        }
        return obj;
    }

}