package com.judokit.android.examples.feature.noui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.TransactionDetails
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

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

/** A transaction the Activity should start on its [com.judopay.judokit.android.JudoCardTransactionClient]. */
sealed class TransactionRequest {
    abstract val details: TransactionDetails

    data class Payment(
        override val details: TransactionDetails,
    ) : TransactionRequest()

    data class PreAuth(
        override val details: TransactionDetails,
    ) : TransactionRequest()

    data class Check(
        override val details: TransactionDetails,
    ) : TransactionRequest()
}

/**
 * The [com.judopay.judokit.android.JudoCardTransactionClient] is bound to the Activity (it registers
 * the 3DS2 challenge launcher), so the Activity owns it: this ViewModel only asks it to start a
 * transaction via [transactionRequest] and receives the outcome via [onResult].
 */
class DemoNoUiPaymentViewModel(
    private val judo: Judo,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ActivityState>(ActivityState.Idle)
    val uiState: StateFlow<ActivityState> = _uiState.asStateFlow()

    private val _transactionRequest = Channel<TransactionRequest>(Channel.BUFFERED)
    val transactionRequest: Flow<TransactionRequest> = _transactionRequest.receiveAsFlow()

    // A Channel rather than a SharedFlow: after process death the result can arrive while the
    // Activity is only CREATED and not yet collecting, and must not be dropped.
    private val _paymentResult = Channel<JudoPaymentResult>(Channel.BUFFERED)
    val paymentResult: Flow<JudoPaymentResult> = _paymentResult.receiveAsFlow()

    val initialCardInput = CardInputState()

    fun payment(cardInput: CardInputState) {
        _uiState.value = ActivityState.PayWithCard
        _transactionRequest.trySend(TransactionRequest.Payment(buildTransactionDetails(judo, cardInput)))
    }

    fun preAuth(cardInput: CardInputState) {
        _uiState.value = ActivityState.PreAuthWithCard
        _transactionRequest.trySend(TransactionRequest.PreAuth(buildTransactionDetails(judo, cardInput)))
    }

    fun check(cardInput: CardInputState) {
        _uiState.value = ActivityState.CheckCard
        _transactionRequest.trySend(TransactionRequest.Check(buildTransactionDetails(judo, cardInput)))
    }

    fun onResult(result: JudoPaymentResult) {
        _uiState.value = ActivityState.Idle
        _paymentResult.trySend(result)
    }

    class Factory(
        private val judo: Judo,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = DemoNoUiPaymentViewModel(judo) as T
    }
}
