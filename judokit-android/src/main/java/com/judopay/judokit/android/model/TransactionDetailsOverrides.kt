package com.judopay.judokit.android.model

data class TransactionDetailsOverrides(
    val softDeclineReceiptId: String? = null,
    val exemption: ScaExemption? = null,
    val challengeRequestIndicator: ChallengeRequestIndicator? = ChallengeRequestIndicator.CHALLENGE_AS_MANDATE
)
