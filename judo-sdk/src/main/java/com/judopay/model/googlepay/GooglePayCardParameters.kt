package com.judopay.model.googlepay

import android.os.Parcelable
import com.judopay.model.CardNetwork
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayCardParameters(
    val allowedAuthMethods: Array<GooglePayAuthMethod>,
    val allowedCardNetworks: Array<CardNetwork>,
    val allowPrepaidCards: Boolean?,
    val billingAddressRequired: Boolean?,
    val billingAddressParameters: GooglePayBillingAddressParameters?
) : Parcelable

