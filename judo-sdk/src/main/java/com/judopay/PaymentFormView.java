package com.judopay;

import android.support.annotation.StringRes;

import com.judopay.payment.Receipt;
import com.judopay.secure3d.ThreeDSecureListener;

interface PaymentFormView {

    void showLoading();

    void hideLoading();

    void finish(Receipt receipt);

    void handleError();

    void showDeclinedMessage(Receipt receipt);

    void setLoadingText(@StringRes int text);

    void start3dSecureWebView(Receipt receipt, ThreeDSecureListener threeDSecureListener);

    void show3dSecureWebView();

}