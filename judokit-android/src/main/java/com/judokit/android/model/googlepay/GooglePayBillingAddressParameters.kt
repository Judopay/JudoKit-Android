package com.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayBillingAddressParameters(
    val format: GooglePayAddressFormat?,
    val phoneNumberRequired: Boolean?
) : Parcelable
