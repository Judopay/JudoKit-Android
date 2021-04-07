package com.judopay.judokit.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ScaExemption(val value: String) : Parcelable {
    LOW_VALUE("lowValue"),
    SECURE_CORPORATE("secureCorporate"),
    TRUSTED_BENEFICIARY("trustedBeneficiary"),
    TRANSACTION_RISK_ANALYSIS("transactionRiskAnalysis")
}
