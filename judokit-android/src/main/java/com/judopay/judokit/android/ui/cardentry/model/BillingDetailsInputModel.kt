package com.judopay.judokit.android.ui.cardentry.model

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.common.ButtonState

data class BillingDetailsInputModel(
    val email: String = "",
    val phoneCountryCode: String = "",
    val mobileNumber: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val addressLine3: String = "",
    val city: String = "",
    val countryCode: String = "",
    val postalCode: String = "",
    val administrativeDivision: String = "",
    var submitButtonState: ButtonState = ButtonState.Disabled(R.string.jp_pay_now),
    var backButtonState: ButtonState = ButtonState.Enabled(R.string.jp_back),
    var isValid: Boolean = false,
)
