package com.judopay.ui.paymentmethods.model

data class NoPaymentMethodSelectedViewModel(
        override val type: CardViewType = CardViewType.PLACEHOLDER
) : CardViewModel