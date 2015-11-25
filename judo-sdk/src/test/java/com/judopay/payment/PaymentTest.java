package com.judopay.payment;

import org.junit.Test;

public class PaymentTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoCurrency() {
        new Payment.Builder()
                .setAmount("9.99")
                .setJudoId(1234567L)
                .setYourPaymentReference("paymentRef")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoAmount() {
        new Payment.Builder()
                .setCurrency("GBP")
                .setJudoId(1234567L)
                .setYourPaymentReference("paymentRef")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoConsumer() {
        new Payment.Builder()
                .setCurrency("GBP")
                .setJudoId(1234567L)
                .setAmount("9.99")
                .setYourPaymentReference("paymentRef")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoPaymentRef() {
        new Payment.Builder()
                .setCurrency("GBP")
                .setJudoId(1234567L)
                .setAmount("9.99")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoJudoId() {
        new Payment.Builder()
                .setCurrency("GBP")
                .setAmount("9.99")
                .setYourPaymentReference("paymentRef")
                .build();
    }

}