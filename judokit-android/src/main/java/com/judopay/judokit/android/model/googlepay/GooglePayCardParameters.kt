package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import com.judopay.judokit.android.model.CardNetwork
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayCardParameters(
    val allowedAuthMethods: Array<GooglePayAuthMethod>,
    val allowedCardNetworks: Array<CardNetwork>,
    val allowPrepaidCards: Boolean?,
    val allowCreditCards: Boolean?,
    val billingAddressRequired: Boolean?,
    val billingAddressParameters: GooglePayBillingAddressParameters?,
) : Parcelable
