package com.judopay.error;

public class JudoIdInvalidError extends Error {

    public JudoIdInvalidError() {
        super("Invalid Judo ID provided, please check it matches the judo ID in your account settings.");
    }

}
