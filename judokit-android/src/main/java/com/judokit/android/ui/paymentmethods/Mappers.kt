package com.judokit.android.ui.paymentmethods

import android.content.Context
import com.judokit.android.api.model.response.CardToken
import com.judokit.android.db.entity.TokenizedCardEntity
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.defaultCardNameResId
import com.judokit.android.ui.editcard.CardPattern
import com.judokit.android.ui.paymentmethods.adapter.model.IdealBank
import com.judokit.android.ui.paymentmethods.adapter.model.IdealBankItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
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
