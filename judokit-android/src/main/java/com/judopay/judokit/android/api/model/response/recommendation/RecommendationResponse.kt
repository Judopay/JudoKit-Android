package com.judopay.judokit.android.api.model.response.recommendation

import com.judopay.judokit.android.model.TransactionDetailsOverrides

data class RecommendationResponse(
    val data: RecommendationData?
) {
    val isValid: Boolean
        get() = data?.isValid ?: false
}

fun RecommendationResponse.toTransactionDetailsOverrides(): TransactionDetailsOverrides? {
    if (!isValid) return null

    val exemption = data?.transactionOptimisation?.exemption?.toScaExemption()
    val challengeRequestIndicator = data?.transactionOptimisation?.threeDSChallengePreference?.toChallengeRequestIndicator()

    if (exemption == null && challengeRequestIndicator == null) return null

    return TransactionDetailsOverrides(
        exemption = exemption,
        challengeRequestIndicator = challengeRequestIndicator
    )
}
