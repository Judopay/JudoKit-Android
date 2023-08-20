package com.judopay.judokit.android.api.model.response

import com.judopay.judokit.android.model.ScaExemption
import com.judopay.judokit.android.model.ThreeDSChallengePreference

data class TransactionOptimisation(
    val exemption: ScaExemption?,
    val threeDSChallengePreference: ThreeDSChallengePreference?
)