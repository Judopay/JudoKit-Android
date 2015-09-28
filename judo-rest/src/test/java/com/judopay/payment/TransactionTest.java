package com.judopay.payment;

import org.junit.Test;

public class TransactionTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCurrencyNull() {
        new Transaction.Builder()
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCurrencyEmpty() {
        new Transaction.Builder()
                .setCurrency("")
                .build();
    }

}