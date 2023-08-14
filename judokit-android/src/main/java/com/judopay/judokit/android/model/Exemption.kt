package com.judopay.judokit.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Exemption : Parcelable {
    LOW_VALUE,
    TRANSACTION_RISK_ANALYSIS
}
