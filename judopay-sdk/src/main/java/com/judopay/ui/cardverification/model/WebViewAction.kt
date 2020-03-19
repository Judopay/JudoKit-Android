package com.judopay.ui.cardverification.model

import com.judopay.api.model.response.CardVerificationResult

sealed class WebViewAction {
    object OnPageStarted : WebViewAction()
    object OnPageLoaded : WebViewAction()
    data class OnAuthorizationComplete(
        val cardVerificationResult: CardVerificationResult,
        val receiptId: String
    ) : WebViewAction()
}
