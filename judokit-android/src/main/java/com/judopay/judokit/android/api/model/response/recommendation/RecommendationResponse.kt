package com.judopay.judokit.android.api.model.response.recommendation

import com.judopay.judokit.android.model.TransactionDetailsOverrides

data class RecommendationResponse(
    val data: RecommendationData?,
) {
    val isValid: Boolean
        get() = data?.isValid ?: false
}

@Suppress("ReturnCount", "SwallowedException")
fun RecommendationResponse.toTransactionDetailsOverrides(): TransactionDetailsOverrides? {
    if (!isValid) return null

    val exemption =
        try {
            data?.transactionOptimisation?.exemption?.toScaExemption()
        } catch (e: IllegalArgumentException) {
            null
        }

    val challengeRequestIndicator =
        try {
            data?.transactionOptimisation?.threeDSChallengePreference?.toChallengeRequestIndicator()
        } catch (e: IllegalArgumentException) {
            null
        }

    return TransactionDetailsOverrides(
        exemption = exemption,
        challengeRequestIndicator = challengeRequestIndicator,
    )
}
