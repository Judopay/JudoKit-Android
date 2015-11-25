package com.judopay.payment;

import com.judopay.model.Payment;

import org.junit.Test;

public class PaymentTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCurrencyNull() {
        new Payment.Builder()
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCurrencyEmpty() {
        new Payment.Builder()
                .setCurrency("")
                .build();
    }

}