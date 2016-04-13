package com.judopay.error;

public class SslInitializationError extends Error {

    public SslInitializationError(Throwable throwable) {
        super("SSL initialization failed", throwable);
    }

}