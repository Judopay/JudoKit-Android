package com.judopay.ui.paymentmethods

import android.content.Context
import com.judopay.api.model.response.CardToken
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.model.CardNetwork
import com.judopay.model.defaultCardNameResId
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.ui.paymentmethods.model.PaymentCardViewModel

fun TokenizedCardEntity.toPaymentMethodSavedCardItem() = PaymentMethodSavedCardItem(
    id = id,
    title = title,
    network = network,
    ending = ending,
    token = token,
    expireDate = expireDate
)

fun PaymentMethodSavedCardItem.toPaymentCardViewModel() = PaymentCardViewModel(
    id = id,
    cardNetwork = network,
    name = title,
    maskedNumber = ending,
    expireDate = expireDate
)

fun CardToken.toTokenizedCardEntity(context: Context): TokenizedCardEntity {
    val network = CardNetwork.withIdentifier(type)
    return TokenizedCardEntity(
        token = token ?: "",
        title = context.getString(network.defaultCardNameResId),
        expireDate = formattedEndDate,
        ending = lastFour ?: "",
        network = network
    )
}
