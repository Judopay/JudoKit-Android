package com.judopay.security;

public class RootUserBlockedException extends RuntimeException {

    public RootUserBlockedException() {
        super("Android Root user not permitted");
    }
}
