package com.judopay.auth;

import com.judopay.JudoPay;
import com.judopay.arch.api.AuthorizationEncoder;

import org.junit.Test;

public class AuthorizationEncoderTest {

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenNoApiToken() {
        JudoPay.setup(null, null, "apiSecret", 0);
        AuthorizationEncoder authorizationEncoder = new AuthorizationEncoder();
        authorizationEncoder.getAuthorization();
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenNoApiSecret() {
        JudoPay.setup(null, "apiToken", null, 0);
        AuthorizationEncoder authorizationEncoder = new AuthorizationEncoder();
        authorizationEncoder.getAuthorization();
    }

}