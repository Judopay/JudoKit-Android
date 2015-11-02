package com.judopay.payment;

public interface PaymentListener {

    void onPaymentSuccess(Receipt receipt);

    void onPaymentDeclined(Receipt receipt);

}