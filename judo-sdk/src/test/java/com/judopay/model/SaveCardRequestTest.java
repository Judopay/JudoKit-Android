package com.judopay.model;

import com.judopay.error.JudoIdInvalidError;

import junit.framework.Assert;

import org.junit.Test;

public class SaveCardRequestTest {

    @Test
    public void shouldBuildWithValidData() {
        SaveCardRequest request = new SaveCardRequest.Builder()
                .setJudoId("100915867")
                .setCardNumber("1234123412341234")
                .setCv2("123")
                .setExpiryDate("12/20")
                .build();
        Assert.assertNotNull(request);
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfNotProvided() {
        new SaveCardRequest.Builder()
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfEmpty() {
        new SaveCardRequest.Builder()
                .setJudoId("")
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfInvalidLuhn() {
        new SaveCardRequest.Builder()
                .setJudoId("123456")
                .build();
    }
}
