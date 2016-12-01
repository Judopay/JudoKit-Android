package com.judopay;

import android.support.annotation.StringRes;

import com.judopay.model.Receipt;
import com.judopay.cardverification.AuthorizationListener;

interface TransactionCallbacks {

    void onSuccess(Receipt receipt);

    void onError(Receipt receipt);

    void onDeclined(Receipt receipt);

    void onConnectionError();

    void showLoading();

    void hideLoading();

    void setLoadingText(@StringRes int text);

    void start3dSecureWebView(Receipt receipt, AuthorizationListener authorizationListener);

    void dismiss3dSecureDialog();

}