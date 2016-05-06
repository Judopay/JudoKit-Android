package com.judopay.api;

import java.util.UUID;

/**
 * A Request to be requested with the judo API
 */
public abstract class Request {

    private final boolean uniqueRequest;
    private final String yourPaymentReference;

    protected Request(String yourPaymentReference) {
        this(false, yourPaymentReference);
    }

    protected Request() {
        this(true, UUID.randomUUID().toString());
    }

    private Request(boolean uniqueRequest, String yourPaymentReference) {
        this.uniqueRequest = uniqueRequest;
        this.yourPaymentReference = yourPaymentReference;
    }

    protected static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new ParameterError();
        }
        return obj;
    }
    public String getYourPaymentReference() {
        return yourPaymentReference;
    }

    public boolean isUniqueRequest() {
        return uniqueRequest;
    }

}