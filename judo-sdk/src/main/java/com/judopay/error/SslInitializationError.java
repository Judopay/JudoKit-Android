package com.judopay.error;

public class SslInitializationError extends Error {

    public SslInitializationError(final Throwable throwable) {
        super("SSL initialization failed", throwable);
    }

}