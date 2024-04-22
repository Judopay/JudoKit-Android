package com.judopay.judokit.android.ui.cardentry.model

import android.os.Parcelable
import com.judopay.judokit.android.model.CardNetwork
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardEntryOptions(
    val isPresentedFromPaymentMethods: Boolean = false,
    val cardNetwork: CardNetwork? = null,
    val isAddingNewCard: Boolean = false,
) : Parcelable
