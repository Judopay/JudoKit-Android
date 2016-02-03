package com.judopay.api;

public class DuplicationTransactionException extends RuntimeException {

    public DuplicationTransactionException(String ref) {
        super(String.format("Cannot make duplicate transaction with same unique ref: %s", ref));
    }

}