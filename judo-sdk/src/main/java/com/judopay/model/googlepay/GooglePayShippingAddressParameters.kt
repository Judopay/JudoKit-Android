package com.judopay.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayShippingAddressParameters(
    val allowedCountryCodes: Array<String>?,
    val phoneNumberRequired: Boolean?
) : Parcelable
