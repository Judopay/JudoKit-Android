package com.judopay.model;

import com.judopay.api.model.request.PaymentRequest;
import com.judopay.api.error.JudoIdInvalidError;

import org.junit.Test;

public class PaymentRequestTest {

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfNotProvided() {
        new PaymentRequest.Builder()
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfEmpty() {
        new PaymentRequest.Builder()
                .setJudoId("")
                .build();
    }

    @Test(expected = JudoIdInvalidError.class)
    public void shouldThrowJudoIdInvalidErrorIfInvalidLuhn() {
        new PaymentRequest.Builder()
                .setJudoId("123456")
                .build();
    }
}
