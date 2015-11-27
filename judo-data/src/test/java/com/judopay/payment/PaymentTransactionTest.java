package com.judopay.payment;

import com.judopay.model.PaymentTransaction;

import org.junit.Test;

public class PaymentTransactionTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCurrencyNull() {
        new PaymentTransaction.Builder()
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCurrencyEmpty() {
        new PaymentTransaction.Builder()
                .setCurrency("")
                .build();
    }

}