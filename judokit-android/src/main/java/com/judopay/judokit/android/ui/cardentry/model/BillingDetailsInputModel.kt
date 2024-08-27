package com.judopay.judokit.android.ui.cardentry.model

import android.content.Context
import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.common.getDefaultCountry

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
    val state: String = "",
    var submitButtonState: ButtonState = ButtonState.Disabled(R.string.pay_now),
    var backButtonState: ButtonState = ButtonState.Enabled(R.string.back),
    var isValid: Boolean = false,
) {
    constructor(context: Context) : this(
        countryCode = getDefaultCountry(context)?.numericCode.orEmpty()
    )
}
