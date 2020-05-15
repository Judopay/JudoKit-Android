package com.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayShippingAddressParameters(
    val allowedCountryCodes: Array<String>? = null,
    val phoneNumberRequired: Boolean?
) : Parcelable
