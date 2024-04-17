package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayBillingAddressParameters(
    val format: GooglePayAddressFormat?,
    val phoneNumberRequired: Boolean?,
) : Parcelable
