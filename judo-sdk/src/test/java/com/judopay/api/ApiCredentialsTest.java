package com.judopay.api;

import com.judopay.api.model.Credentials;
import com.judopay.api.error.TokenSecretError;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApiCredentialsTest {

    @Test(expected = TokenSecretError.class)
    public void shouldThrowTokenSecretErrorIfTokenEmpty() {
        new Credentials("", "apiSecret");
    }

    @Test(expected = TokenSecretError.class)
    public void shouldThrowTokenSecretErrorIfSecretEmpty() {
        new Credentials("apiToken", "");
    }
}
