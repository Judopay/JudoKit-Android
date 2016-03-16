package com.judopay;

import android.support.annotation.StringRes;

import com.judopay.model.Receipt;
import com.judopay.secure3d.ThreeDSecureListener;

interface TransactionCallbacks {

    void onSuccess(Receipt receipt);

    void onError(Receipt receipt);

    void onDeclined(Receipt receipt);

    void onConnectionError();

    void showLoading();

    void hideLoading();

    void setLoadingText(@StringRes int text);

    void start3dSecureWebView(Receipt receipt, ThreeDSecureListener threeDSecureListener);

    void show3dSecureWebView();

    void dismiss3dSecureDialog();

}