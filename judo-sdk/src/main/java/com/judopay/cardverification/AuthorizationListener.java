package com.judopay.cardverification;

import com.judopay.model.CardVerificationResult;

/**
 * Listener for notifying of page loading events for 3D-Secure
 */
public interface AuthorizationListener {

    void onAuthorizationCompleted(CardVerificationResult cardVerificationResult, String receiptId);

}