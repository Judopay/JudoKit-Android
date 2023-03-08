package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayShippingAddressParameters(
    val allowedCountryCodes: Array<String>? = null,
    val phoneNumberRequired: Boolean?
) : Parcelable
