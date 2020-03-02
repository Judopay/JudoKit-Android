package com.judopay.ui.paymentmethods.model

data class PaymentCardViewModel(
        override val type: CardViewType = CardViewType.CARD,
        val name: String = "",
        val maskedNumber: String = "",
        val expireDate: String = ""
) : CardViewModel