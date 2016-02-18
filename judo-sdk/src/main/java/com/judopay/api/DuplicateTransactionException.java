package com.judopay.api;

public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String ref) {
        super(String.format("Cannot make duplicate transaction with same unique ref: %s", ref));
    }

}