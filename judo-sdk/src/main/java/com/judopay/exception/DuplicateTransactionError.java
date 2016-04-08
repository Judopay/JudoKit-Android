package com.judopay.exception;

public class DuplicateTransactionError extends Error {

    public DuplicateTransactionError(String ref) {
        super(String.format("Cannot make duplicate transaction with same unique ref: %s", ref));
    }

}