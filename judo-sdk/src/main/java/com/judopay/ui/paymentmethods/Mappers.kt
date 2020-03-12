package com.judopay.ui.paymentmethods

import android.content.Context
import com.judopay.api.model.response.CardToken
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.model.CardNetwork
import com.judopay.model.defaultCardNameResId
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.ui.paymentmethods.components.PaymentCardViewModel

fun TokenizedCardEntity.toPaymentMethodSavedCardItem() = PaymentMethodSavedCardItem(
        id = id,
        title = title,
        network = network,
        ending = ending,
        token = token,
        expireDate = "03/20"
)

fun PaymentMethodSavedCardItem.toPaymentCardViewModel() = PaymentCardViewModel(
        id = id,
        cardNetwork = network,
        name = title,
        maskedNumber = ending,
        expireDate = "03/20"
)

fun CardToken.toTokenizedCardEntity(context: Context): TokenizedCardEntity {
    val network = CardNetwork.withIdentifier(type)
    return TokenizedCardEntity(
            token = token ?: "",
            title = context.getString(network.defaultCardNameResId),
            expireDate = formattedEndDate,
            ending = lastFour ?: "",
            network = network)
}
