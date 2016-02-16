package com.judopay.api;

import com.judopay.model.ClientDetails;

import java.util.UUID;

/**
 * A request to be sent to the judo API
 */
public abstract class Request {

    private final boolean uniqueRequest;
    private final String yourPaymentReference;

    private final ClientDetails clientDetails;

    protected Request(String yourPaymentReference) {
        this(false, yourPaymentReference);
    }

    protected Request(boolean uniqueRequest) {
        this(uniqueRequest, UUID.randomUUID().toString());
    }

    Request(boolean uniqueRequest, String yourPaymentReference) {
        this.uniqueRequest = uniqueRequest;
        this.yourPaymentReference = yourPaymentReference;
        clientDetails = new ClientDetails();
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
    }

    public boolean isUniqueRequest() {
        return uniqueRequest;
    }

    public ClientDetails getClientDetails() {
        return clientDetails;
    }

}