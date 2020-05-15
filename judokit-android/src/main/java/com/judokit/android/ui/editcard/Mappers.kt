package com.judokit.android.ui.editcard

import com.judokit.android.db.entity.TokenizedCardEntity
import com.judokit.android.ui.paymentmethods.model.PaymentCardViewModel

fun TokenizedCardEntity.toPaymentCardViewModel(newTitle: String, selectedPattern: CardPattern) =
    PaymentCardViewModel(
        id = id,
        cardNetwork = network,
        name = newTitle,
        maskedNumber = ending,
        expireDate = expireDate,
        pattern = selectedPattern
    )
