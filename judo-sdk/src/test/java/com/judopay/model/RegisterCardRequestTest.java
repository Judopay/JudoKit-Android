package com.judopay.model;

import com.judopay.error.JudoIdInvalidError;

import org.junit.Test;

public class RegisterCardRequestTest {

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfNotProvided() {
        new RegisterCardRequest.Builder()
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfEmpty() {
        new RegisterCardRequest.Builder()
                .setJudoId("")
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfInvalidLuhn() {
        new RegisterCardRequest.Builder()
                .setJudoId("123456")
                .build();
    }
}
