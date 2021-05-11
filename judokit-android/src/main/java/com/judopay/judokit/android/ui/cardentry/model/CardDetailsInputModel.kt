package com.judopay.judokit.android.ui.cardentry.model

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.Country
import com.judopay.judokit.android.model.displayName
import com.judopay.judokit.android.ui.common.ButtonState

data class CardDetailsInputModel(
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expirationDate: String = "",
    val securityNumber: String = "",
    val country: String = Country.GB.displayName,
    val postCode: String = "",
    var buttonState: ButtonState = ButtonState.Disabled(R.string.add_card),
    var enabledFields: List<FormFieldType> = emptyList(),
    var supportedNetworks: List<CardNetwork> = emptyList(),
    var cardNetwork: CardNetwork? = null
)
