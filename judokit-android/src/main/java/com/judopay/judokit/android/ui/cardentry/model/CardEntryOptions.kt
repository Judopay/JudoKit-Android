package com.judopay.judokit.android.ui.cardentry.model

import android.os.Parcelable
import com.judopay.judokit.android.model.CardNetwork
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardEntryOptions(
    val fromPaymentMethods: Boolean = false,
    val shouldDisplayBillingDetails: Boolean = false,
    val shouldDisplaySecurityCode: CardNetwork? = null,
    val addCardPressed: Boolean = false
) : Parcelable
