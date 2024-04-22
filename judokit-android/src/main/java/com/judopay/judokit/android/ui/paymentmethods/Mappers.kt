package com.judopay.judokit.android.ui.paymentmethods

import android.content.Context
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.defaultCardNameResId
import com.judopay.judokit.android.ui.editcard.CardPattern
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import kotlin.random.Random

fun TokenizedCardEntity.toPaymentMethodSavedCardItem() =
    PaymentMethodSavedCardItem(
        id = id,
        title = title,
        network = network,
        ending = ending,
        token = token,
        expireDate = expireDate,
        pattern = pattern,
        cardholderName = cardholderName,
    )

fun PaymentMethodSavedCardItem.toPaymentCardViewModel() =
    PaymentCardViewModel(
        id = id,
        cardNetwork = network,
        name = title,
        maskedNumber = ending,
        expireDate = expireDate,
        pattern = pattern,
        cardholderName = cardholderName,
    )

fun CardToken.toTokenizedCardEntity(
    context: Context,
    cardholderName: String,
): TokenizedCardEntity {
    val network = CardNetwork.withIdentifier(type, scheme)
    val patterns = CardPattern.values()
    return TokenizedCardEntity(
        token = token ?: "",
        title = context.getString(network.defaultCardNameResId),
        expireDate = formattedEndDate,
        ending = lastFour ?: "",
        network = network,
        pattern = patterns[Random.nextInt(patterns.size)],
        cardholderName = cardholderName,
    )
}
