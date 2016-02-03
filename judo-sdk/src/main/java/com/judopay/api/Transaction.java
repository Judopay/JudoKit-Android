package com.judopay.api;

import com.judopay.model.ClientDetails;

import java.util.UUID;

/**
 * A Transaction to be requested with the judo API
 */
public abstract class Transaction {

    private final boolean uniqueRequest;
    private final String yourPaymentReference;

    private final ClientDetails clientDetails;

    public Transaction(String yourPaymentReference) {
        this(false, yourPaymentReference);
    }

    public Transaction(boolean uniqueRequest) {
        this(uniqueRequest, UUID.randomUUID().toString());
    }

    Transaction(boolean uniqueRequest, String yourPaymentReference) {
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