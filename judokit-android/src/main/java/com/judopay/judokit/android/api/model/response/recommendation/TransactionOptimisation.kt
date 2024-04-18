package com.judopay.judokit.android.api.model.response.recommendation

import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.ScaExemption

private const val LOW_VALUE = "LOW_VALUE"
private const val TRANSACTION_RISK_ANALYSIS = "TRANSACTION_RISK_ANALYSIS"

private const val NO_PREFERENCE = "NO_PREFERENCE"
private const val NO_CHALLENGE_REQUESTED = "NO_CHALLENGE_REQUESTED"
private const val CHALLENGE_REQUESTED = "CHALLENGE_REQUESTED"
private const val CHALLENGE_REQUESTED_AS_MANDATE = "CHALLENGE_REQUESTED_AS_MANDATE"

fun String?.toChallengeRequestIndicator(): ChallengeRequestIndicator? {
    return when (this?.uppercase()) {
        NO_PREFERENCE -> ChallengeRequestIndicator.NO_PREFERENCE
        NO_CHALLENGE_REQUESTED -> ChallengeRequestIndicator.NO_CHALLENGE
        CHALLENGE_REQUESTED -> ChallengeRequestIndicator.CHALLENGE_PREFERRED
        CHALLENGE_REQUESTED_AS_MANDATE -> ChallengeRequestIndicator.CHALLENGE_AS_MANDATE
        null -> null
        else -> ChallengeRequestIndicator.ERROR_CHALLENGE_NOT_RECOGNIZED
    }
}

fun String?.toScaExemption(): ScaExemption? {
    return when (this?.uppercase()) {
        LOW_VALUE -> ScaExemption.LOW_VALUE
        TRANSACTION_RISK_ANALYSIS -> ScaExemption.TRANSACTION_RISK_ANALYSIS
        null -> null
        else -> ScaExemption.ERROR_EXEMPTION_NOT_RECOGNIZED
    }
}

data class TransactionOptimisation(
    val exemption: String?,
    val threeDSChallengePreference: String?
) {
    val isValid: Boolean
        get() {
            return (
                exemption.toScaExemption() != ScaExemption.ERROR_EXEMPTION_NOT_RECOGNIZED &&
                    threeDSChallengePreference.toChallengeRequestIndicator() != ChallengeRequestIndicator.ERROR_CHALLENGE_NOT_RECOGNIZED
                )
        }
}
