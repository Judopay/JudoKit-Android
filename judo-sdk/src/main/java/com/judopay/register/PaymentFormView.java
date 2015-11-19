package com.judopay.register;

import android.support.annotation.StringRes;

import com.judopay.payment.Receipt;

interface PaymentFormView {

    void showLoading();

    void hideLoading();

    void finish(Receipt receipt);

    void showDeclinedMessage(Receipt receipt);

    void setLoadingText(@StringRes int text);

    void start3dSecureWebView(Receipt receipt);

    void show3dSecureWebView();

}