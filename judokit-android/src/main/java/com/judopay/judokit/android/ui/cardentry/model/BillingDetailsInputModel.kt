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
    val submitButtonState: ButtonState = ButtonState.Disabled(R.string.jp_pay_now),
    val backButtonState: ButtonState = ButtonState.Enabled(R.string.jp_back),
    val adminDivisionRequired: Boolean = false,
    /*
     * Key absent = not yet validated (invalid for form purposes).
     * Key present + null = valid, no error to display.
     * Key present + non-null = invalid, display this @StringRes error message.
     */
    val fieldErrors: Map<BillingDetailsFieldType, Int?> = emptyMap(),
)
