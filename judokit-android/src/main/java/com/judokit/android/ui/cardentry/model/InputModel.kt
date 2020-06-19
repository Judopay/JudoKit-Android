package com.judokit.android.ui.cardentry.model

import com.judokit.android.model.Country
import com.judokit.android.model.displayName

data class InputModel(
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expirationDate: String = "",
    val securityNumber: String = "",
    val country: String = Country.GB.displayName,
    val postCode: String = ""
)
