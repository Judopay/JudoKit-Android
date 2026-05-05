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
    val actionButtonState: ButtonState = ButtonState.Disabled(R.string.jp_add_card),
    val enabledFields: List<CardDetailsFieldType> = emptyList(),
    val supportedNetworks: List<CardNetwork> = emptyList(),
    val cardNetwork: CardNetwork? = null,
    /*
     * Key absent = not yet validated (invalid for form purposes).
     * Key present + null = valid, no error to display.
     * Key present + non-null = invalid, display this @StringRes error message.
     */
    val fieldErrors: Map<CardDetailsFieldType, Int?> = emptyMap(),
)
