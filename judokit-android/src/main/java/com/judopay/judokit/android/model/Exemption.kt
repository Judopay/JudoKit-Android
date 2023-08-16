package com.judopay.judokit.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Todo: Confirm this class can be removed.
@Parcelize
enum class Exemption : Parcelable {
    LOW_VALUE,
    TRANSACTION_RISK_ANALYSIS
}

fun Exemption.toScaExemption(): ScaExemption {
    return when (this) {
        Exemption.LOW_VALUE -> ScaExemption.LOW_VALUE
        Exemption.TRANSACTION_RISK_ANALYSIS -> ScaExemption.TRANSACTION_RISK_ANALYSIS
    }
}