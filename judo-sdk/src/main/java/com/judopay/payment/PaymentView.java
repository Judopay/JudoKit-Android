package com.judopay.payment;

public interface PaymentView {

    void showLoading();

    void hideLoading();

    void setViewModel(PaymentResponse paymentResponse);

}