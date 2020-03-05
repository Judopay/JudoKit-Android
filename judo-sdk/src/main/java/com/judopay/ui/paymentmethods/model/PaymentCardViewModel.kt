package com.judopay.ui.paymentmethods.model

import com.judopay.model.CardNetwork

data class PaymentCardViewModel(
        override val type: CardViewType = CardViewType.CARD,
        val cardNetwork: CardNetwork = CardNetwork.VISA,
        val name: String = "",
        val maskedNumber: String = "",
        val expireDate: String = ""
) : CardViewModel