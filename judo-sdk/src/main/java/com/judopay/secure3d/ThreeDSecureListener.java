package com.judopay.secure3d;

import com.judopay.model.ThreeDSecureInfo;

public interface ThreeDSecureListener {

    void onAuthorizationWebPageLoaded();

    void onAuthorizationCompleted(ThreeDSecureInfo threeDSecureInfo, String receiptId);

    void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl);

}