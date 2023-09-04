package com.judopay.judokit.android.api.model.response

import com.judopay.judokit.android.model.ScaExemption

data class TransactionOptimisation(
    val exemption: ScaExemption?,
    val threeDSChallengePreference: String?
)
