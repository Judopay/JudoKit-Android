package com.judopay.secure3d;

import com.judopay.model.ThreeDSecureInfo;

/**
 * Listener for notifying of authorization page loading events for 3D-Secure
 */
public interface ThreeDSecureListener {

    void onAuthorizationWebPageLoaded();

    void onAuthorizationCompleted(ThreeDSecureInfo threeDSecureInfo, String receiptId);

}