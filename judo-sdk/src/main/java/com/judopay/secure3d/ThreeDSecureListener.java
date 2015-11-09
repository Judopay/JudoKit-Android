package com.judopay.secure3d;

import com.judopay.payment.ThreeDSecureInfo;

public interface ThreeDSecureListener {

    void onAuthorizationCompleted(ThreeDSecureInfo threeDSecureInfo, String receiptId);

    void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl);

}