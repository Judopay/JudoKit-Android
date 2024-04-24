package com.judopay.judokit.android.ui.editcard

import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel

fun TokenizedCardEntity.toPaymentCardViewModel(
    newTitle: String,
    selectedPattern: CardPattern,
) = PaymentCardViewModel(
    id = id,
    cardNetwork = network,
    name = newTitle,
    maskedNumber = ending,
    expireDate = expireDate,
    pattern = selectedPattern,
)
