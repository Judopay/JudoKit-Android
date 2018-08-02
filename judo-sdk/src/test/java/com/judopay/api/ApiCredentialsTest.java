package com.judopay.api;

import com.judopay.error.TokenSecretError;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApiCredentialsTest {

    @Test(expected = TokenSecretError.class)
    public void shouldThrowTokenSecretErrorIfTokenEmpty() {
        new ApiCredentials("", "apiSecret");
    }

    @Test(expected = TokenSecretError.class)
    public void shouldThrowTokenSecretErrorIfSecretEmpty() {
        new ApiCredentials("apiToken", "");
    }
}
