package com.judopay.payment;

import com.judopay.Payment;

import org.junit.Test;

public class PaymentTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoCurrency() {
        new Payment.Builder()
                .setAmount("9.99")
                .setJudoId("1234567")
                .setPaymentRef("paymentRef")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoAmount() {
        new Payment.Builder()
                .setCurrency("GBP")
                .setJudoId("1234567")
                .setPaymentRef("paymentRef")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoConsumer() {
        new Payment.Builder()
                .setCurrency("GBP")
                .setJudoId("1234567")
                .setAmount("9.99")
                .setPaymentRef("paymentRef")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoPaymentRef() {
        new Payment.Builder()
                .setCurrency("GBP")
                .setJudoId("1234567")
                .setAmount("9.99")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoJudoId() {
        new Payment.Builder()
                .setCurrency("GBP")
                .setAmount("9.99")
                .setPaymentRef("paymentRef")
                .build();
    }

}