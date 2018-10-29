package com.judopay.cardverification;

import com.judopay.model.CardVerificationResult;
import com.judopay.model.Receipt;

import io.reactivex.Single;

/**
 * Listener for notifying of page loading events for 3D-Secure
 */
public interface AuthorizationListener {

    Single<Receipt> onAuthorizationCompleted(CardVerificationResult cardVerificationResult, String receiptId);
}
