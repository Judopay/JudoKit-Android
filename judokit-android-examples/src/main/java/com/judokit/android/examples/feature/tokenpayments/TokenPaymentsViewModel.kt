package com.judokit.android.examples.feature.tokenpayments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.isTokenPayment

class TokenPaymentsViewModel {
    lateinit var initialJudoConfig: Judo
    var scheme by mutableStateOf("")
    var token by mutableStateOf("")
    var lastFour by mutableStateOf("")
    var securityCode by mutableStateOf("")
    var cardholderName by mutableStateOf("")
    var isBusy by mutableStateOf(false)

    fun getJudo(widgetType: PaymentWidgetType): Judo =
        with(initialJudoConfig) {
            Judo.Builder(widgetType)
                .setJudoId(judoId)
                .setAuthorization(authorization)
                .setIsSandboxed(isSandboxed)
                .setAmount(amount)
                .setReference(reference)
                .setUiConfiguration(uiConfiguration)
                .setPaymentMethods(paymentMethods.clone())
                .setSupportedCardNetworks(supportedCardNetworks.clone())
                .setPrimaryAccountDetails(primaryAccountDetails)
                .setAddress(address)
                .setInitialRecurringPayment(initialRecurringPayment)
                .setNetworkTimeout(networkTimeout)
                .setChallengeRequestIndicator(challengeRequestIndicator)
                .setScaExemption(scaExemption)
                .setMobileNumber(mobileNumber)
                .setPhoneCountryCode(phoneCountryCode)
                .setEmailAddress(emailAddress)
                .setThreeDSTwoMaxTimeout(threeDSTwoMaxTimeout)
                .setThreeDSTwoMessageVersion(threeDSTwoMessageVersion)
                .setDelayedAuthorisation(delayedAuthorisation)
                .setAllowIncrement(allowIncrement)
                .setCardToken(if (widgetType.isTokenPayment) getCardToken() else null)
                .setCardSecurityCode(securityCode.ifBlank { null })
                .build()
        }

    private fun getCardToken() = CardToken(lastFour = lastFour, token = token, type = scheme.toInt(), cardHolderName = cardholderName)
}
