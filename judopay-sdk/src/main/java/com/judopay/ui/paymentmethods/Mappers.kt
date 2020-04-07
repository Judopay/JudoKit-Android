package com.judopay.ui.paymentmethods

import android.content.Context
import com.judopay.api.model.response.CardToken
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.model.CardNetwork
import com.judopay.model.defaultCardNameResId
import com.judopay.ui.editcard.CardPattern
import com.judopay.ui.paymentmethods.adapter.model.IdealBank
import com.judopay.ui.paymentmethods.adapter.model.IdealBankItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.ui.paymentmethods.model.PaymentCardViewModel
import kotlin.random.Random

fun TokenizedCardEntity.toPaymentMethodSavedCardItem() = PaymentMethodSavedCardItem(
    id = id,
    title = title,
    network = network,
    ending = ending,
    token = token,
    expireDate = expireDate,
    pattern = pattern
)

fun IdealBank.toIdealBankItem() = IdealBankItem(idealBank = this)

fun PaymentMethodSavedCardItem.toPaymentCardViewModel() = PaymentCardViewModel(
    id = id,
    cardNetwork = network,
    name = title,
    maskedNumber = ending,
    expireDate = expireDate,
    pattern = pattern
)

fun CardToken.toTokenizedCardEntity(context: Context): TokenizedCardEntity {
    val network = CardNetwork.withIdentifier(type)
    val patterns = CardPattern.values()
    return TokenizedCardEntity(
        token = token ?: "",
        title = context.getString(network.defaultCardNameResId),
        expireDate = formattedEndDate,
        ending = lastFour ?: "",
        network = network,
        pattern = patterns[Random.nextInt(patterns.size)]
    )
}
