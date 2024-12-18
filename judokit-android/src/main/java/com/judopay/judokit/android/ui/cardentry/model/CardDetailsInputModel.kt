package com.judopay.judokit.android.ui.cardentry.model

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.AVSCountry
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.displayName
import com.judopay.judokit.android.ui.common.ButtonState

data class CardDetailsInputModel(
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expirationDate: String = "",
    val securityNumber: String = "",
    val country: String = AVSCountry.GB.displayName,
    val postCode: String = "",
    var actionButtonState: ButtonState = ButtonState.Disabled(R.string.jp_add_card),
    var enabledFields: List<CardDetailsFieldType> = emptyList(),
    var supportedNetworks: List<CardNetwork> = emptyList(),
    var cardNetwork: CardNetwork? = null,
    var isValid: Boolean = false,
)
