package com.judopay.judokit.android.api.model.response

import com.judopay.judokit.android.model.Exemption
import com.judopay.judokit.android.model.ThreeDSChallengePreference

data class TransactionOptimisation(
    val exemption: Exemption,
    val threeDSChallengePreference: ThreeDSChallengePreference
)