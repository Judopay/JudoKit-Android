package com.judopay.model;

import com.judopay.error.JudoIdInvalidError;

import org.junit.Test;

public class TokenRequestTest {

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfNotProvided() {
        new TokenRequest.Builder()
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfEmpty() {
        new TokenRequest.Builder()
                .setJudoId("")
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfInvalidLuhn() {
        new TokenRequest.Builder()
                .setJudoId("123456")
                .build();
    }
}
