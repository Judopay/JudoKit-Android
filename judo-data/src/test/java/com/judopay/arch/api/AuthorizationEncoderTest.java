package com.judopay.arch.api;

import com.judopay.JudoPay;
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