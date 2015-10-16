package com.judopay.payment;

public interface PaymentListener {

    void onPaymentSuccess(PaymentResponse paymentResponse);

    void onPaymentDeclined(PaymentResponse paymentResponse);

}