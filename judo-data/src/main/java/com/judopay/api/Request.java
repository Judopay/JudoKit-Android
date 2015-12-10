package com.judopay.api;

import com.judopay.model.ClientDetails;

/**
 * Base class for all HTTP responses that come back from the JudoPay API.
 */
public abstract class Request {

    private ClientDetails clientDetails = new ClientDetails();

}