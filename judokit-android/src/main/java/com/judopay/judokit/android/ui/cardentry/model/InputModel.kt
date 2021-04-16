package com.judopay.judokit.android.ui.cardentry.model

import com.judopay.judokit.android.model.Country
import com.judopay.judokit.android.model.displayName

data class InputModel(
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expirationDate: String = "",
    val securityNumber: String = "",
    val country: String = Country.GB.displayName,
    val postCode: String = "",
    val email: String = "",
    val phoneCountryCode: String = "",
    val mobileNumber: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val addressLine3: String = "",
    val city: String = "",
    val postalCode: String = ""
)
