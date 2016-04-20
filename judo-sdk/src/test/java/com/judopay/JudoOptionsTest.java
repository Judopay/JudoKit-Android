package com.judopay;

import com.judopay.error.JudoIdInvalidError;

import org.junit.Test;

public class JudoOptionsTest {

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfNotProvided() {
        new JudoOptions.Builder()
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfEmpty() {
        new JudoOptions.Builder()
                .setJudoId("")
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfInvalidLuhn() {
        new JudoOptions.Builder()
                .setJudoId("123456")
                .build();
    }

}