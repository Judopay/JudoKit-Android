package com.judokit.android.examples.feature.noui

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.JudoCardTransactionClient
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.TransactionDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ActivityState {
    object Idle : ActivityState()

    object PayWithCard : ActivityState()

    object PreAuthWithCard : ActivityState()

    object CheckCard : ActivityState()
}

data class CardInputState(
    val cardNumber: String = "4000000000000002",
    val expirationDate: String = "12/30",
    val cardholderName: String = "CHALLENGE",
    val securityNumber: String = "543",
)

private fun buildTransactionDetails(
    judo: Judo,
    cardInput: CardInputState,
): TransactionDetails =
    TransactionDetails
        .Builder()
        .setEmail(judo.emailAddress)
        .setCountryCode(judo.address?.countryCode.toString())
        .setPhoneCountryCode(judo.phoneCountryCode)
        .setMobileNumber(judo.mobileNumber)
        .setAddressLine1(judo.address?.line1)
        .setAddressLine2(judo.address?.line2)
        .setAddressLine3(judo.address?.line3)
        .setCity(judo.address?.town)
        .setPostalCode(judo.address?.postCode)
        .setAdministrativeDivision(judo.address?.state)
        .setCardNumber(cardInput.cardNumber)
        .setExpirationDate(cardInput.expirationDate)
        .setCardHolderName(cardInput.cardholderName)
        .setSecurityNumber(cardInput.securityNumber)
        .build()

class DemoNoUiPaymentViewModel(
    private val client: JudoCardTransactionClient,
    private val judo: Judo,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ActivityState>(ActivityState.Idle)
    val uiState: StateFlow<ActivityState> = _uiState.asStateFlow()

    private val _paymentResult = MutableSharedFlow<JudoPaymentResult>(extraBufferCapacity = 1)
    val paymentResult: SharedFlow<JudoPaymentResult> = _paymentResult.asSharedFlow()

    val initialCardInput = CardInputState()

    fun payment(
        activity: FragmentActivity,
        cardInput: CardInputState,
    ) {
        _uiState.value = ActivityState.PayWithCard
        client.payment(activity, buildTransactionDetails(judo, cardInput)) { onResult(it) }
    }

    fun preAuth(
        activity: FragmentActivity,
        cardInput: CardInputState,
    ) {
        _uiState.value = ActivityState.PreAuthWithCard
        client.preAuth(activity, buildTransactionDetails(judo, cardInput)) { onResult(it) }
    }

    fun check(
        activity: FragmentActivity,
        cardInput: CardInputState,
    ) {
        _uiState.value = ActivityState.CheckCard
        client.check(activity, buildTransactionDetails(judo, cardInput)) { onResult(it) }
    }

    private fun onResult(result: JudoPaymentResult) {
        _uiState.value = ActivityState.Idle
        _paymentResult.tryEmit(result)
    }

    class Factory(
        context: Context,
        private val judo: Judo,
    ) : ViewModelProvider.Factory {
        private val client = JudoCardTransactionClient.create(context, judo)

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = DemoNoUiPaymentViewModel(client, judo) as T
    }
}
