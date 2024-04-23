package com.judopay.judokit.android.api.model.response.recommendation

import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.ScaExemption

private const val LOW_VALUE = "LOW_VALUE"
private const val TRANSACTION_RISK_ANALYSIS = "TRANSACTION_RISK_ANALYSIS"

private const val NO_PREFERENCE = "NO_PREFERENCE"
private const val NO_CHALLENGE_REQUESTED = "NO_CHALLENGE_REQUESTED"
private const val CHALLENGE_REQUESTED = "CHALLENGE_REQUESTED"
private const val CHALLENGE_REQUESTED_AS_MANDATE = "CHALLENGE_REQUESTED_AS_MANDATE"

@Throws(IllegalArgumentException::class)
fun String?.toChallengeRequestIndicator(): ChallengeRequestIndicator? {
    if (this == null) return null

    return when (uppercase()) {
        NO_PREFERENCE -> ChallengeRequestIndicator.NO_PREFERENCE
        NO_CHALLENGE_REQUESTED -> ChallengeRequestIndicator.NO_CHALLENGE
        CHALLENGE_REQUESTED -> ChallengeRequestIndicator.CHALLENGE_PREFERRED
        CHALLENGE_REQUESTED_AS_MANDATE -> ChallengeRequestIndicator.CHALLENGE_AS_MANDATE
        else -> throw IllegalArgumentException("Invalid challenge request indicator value: $this")
    }
}

@Throws(IllegalArgumentException::class)
fun String?.toScaExemption(): ScaExemption? {
    if (this == null) return null

    return when (uppercase()) {
        LOW_VALUE -> ScaExemption.LOW_VALUE
        TRANSACTION_RISK_ANALYSIS -> ScaExemption.TRANSACTION_RISK_ANALYSIS
        else -> throw IllegalArgumentException("Invalid SCA exemption value: $this")
    }
}

@Suppress("SwallowedException")
data class TransactionOptimisation(
    val exemption: String?,
    val threeDSChallengePreference: String?,
) {
    val isValid: Boolean
        get() =
            try {
                exemption?.toScaExemption()
                threeDSChallengePreference?.toChallengeRequestIndicator()
                true
            } catch (e: IllegalArgumentException) {
                false
            }
}
