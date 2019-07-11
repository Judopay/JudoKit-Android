package com.judopay;

import com.judopay.error.JudoIdInvalidError;

import org.junit.Test;

public class JudoTest {

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfNotProvided() {
        new Judo.Builder("apiToken", "apiSecret")
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfEmpty() {
        new Judo.Builder("apiToken", "apiSecret")
                .setJudoId("")
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfInvalidLuhn() {
        new Judo.Builder("apiToken", "apiSecret")
                .setJudoId("123456")
                .build();
    }
}
