package com.judopay.judokit.android.ui.paymentmethods

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.response.CardDate
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.formatted
import com.judopay.judokit.android.model.paymentButtonType
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.service.ChallengeData
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemType
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.judokit.android.ui.paymentmethods.components.GooglePayCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.NoPaymentMethodSelectedViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentCallToActionViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.CardPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.CardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.GooglePayPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentMethodModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// view-model actions
sealed class PaymentMethodsAction {
    data class DeleteCard(
        val cardId: Int,
    ) : PaymentMethodsAction()

    data class SelectPaymentMethod(
        val method: PaymentMethod,
    ) : PaymentMethodsAction()

    data class SelectStoredCard(
        val id: Int,
    ) : PaymentMethodsAction()

    data class UpdateButtonState(
        val buttonEnabled: Boolean,
    ) : PaymentMethodsAction()

    data class EditMode(
        val isInEditMode: Boolean,
    ) : PaymentMethodsAction()

    object InitiateSelectedCardPayment : PaymentMethodsAction()

    data class PayWithCard(
        val transactionDetail: TransactionDetails.Builder,
    ) : PaymentMethodsAction()

    object Update : PaymentMethodsAction()
}

class PaymentMethodsViewModel
    internal constructor(
        private val cardDate: CardDate,
        private val cardRepository: TokenizedCardRepository,
        private val cardTransactionRepository: CardTransactionRepository,
        application: Application,
        private val judo: Judo,
    ) : AndroidViewModel(application) {
        private val _uiState = MutableStateFlow<PaymentMethodsModel?>(null)
        val uiState: StateFlow<PaymentMethodsModel?> = _uiState.asStateFlow()

        private val _paymentResultEffect = MutableSharedFlow<JudoPaymentResult>(extraBufferCapacity = 1)
        val paymentResultEffect: SharedFlow<JudoPaymentResult> = _paymentResultEffect

        private val _cardEntryEffect = MutableSharedFlow<CardEntryOptions>(extraBufferCapacity = 1)
        val cardEntryEffect: SharedFlow<CardEntryOptions> = _cardEntryEffect

        /**
         * Holds the active 3DS2 challenge data while a native challenge is in progress, null otherwise.
         * [StateFlow] ensures a Fragment recreated during a configuration change immediately receives
         * the pending challenge and can re-invoke doChallenge with the fresh Activity.
         */
        private val mutablePendingChallenge = MutableStateFlow<ChallengeData?>(null)
        internal val pendingChallenge: StateFlow<ChallengeData?> = mutablePendingChallenge.asStateFlow()

        private val challengeResultChannel = Channel<String?>(1)

        private val challengeRunner: com.judopay.judokit.android.service.ChallengeRunner = { transaction, params ->
            mutablePendingChallenge.value = ChallengeData(transaction, params)
            challengeResultChannel.receive()
        }

        private var cardList: List<TokenizedCardEntity> = emptyList()

        private val selectedPaymentMethod: PaymentMethod
            get() = _uiState.value?.currentPaymentMethod?.type ?: judo.paymentMethods.first()

        private val selectedCardIdentifier: Int
            get() {
                _uiState.value?.let { myModel ->
                    val method = myModel.currentPaymentMethod
                    if (method is CardPaymentMethodModel) {
                        return method.selectedCard?.id ?: -1
                    }
                }
                return -1
            }

        init {
            viewModelScope.launch {
                cardRepository.allCards.collect { list ->
                    cardList = list
                    buildModel(
                        isLoading =
                            _uiState.value
                                ?.headerModel
                                ?.callToActionModel
                                ?.paymentButtonState == ButtonState.Loading,
                    )
                }
            }
        }

        /**
         * Called by the Fragment after the native 3DS2 challenge completes.
         * Clears the pending challenge state and forwards the result. The first call wins;
         * any duplicate from a stale receiver is silently dropped (channel already has a value).
         */
        fun onChallengeResult(status: String?) {
            mutablePendingChallenge.value = null
            challengeResultChannel.trySend(status)
        }

        @Suppress("CyclomaticComplexMethod")
        fun send(action: PaymentMethodsAction) {
            when (action) {
                is PaymentMethodsAction.DeleteCard -> {
                    deleteCardWithId(action.cardId)
                }
                is PaymentMethodsAction.InitiateSelectedCardPayment -> {
                    buildModel(isLoading = true)
                    val paymentMethod = _uiState.value?.currentPaymentMethod
                    payWithSelectedCard(paymentMethod)
                }
                is PaymentMethodsAction.SelectStoredCard -> {
                    buildModel(isLoading = false, selectedCardId = action.id)
                }
                is PaymentMethodsAction.Update ->
                    buildModel(
                        isLoading =
                            _uiState.value
                                ?.headerModel
                                ?.callToActionModel
                                ?.paymentButtonState == ButtonState.Loading,
                    )
                is PaymentMethodsAction.SelectPaymentMethod -> {
                    if (selectedPaymentMethod != action.method) buildModel(action.method, false)
                }
                is PaymentMethodsAction.UpdateButtonState ->
                    buildModel(
                        isLoading = !action.buttonEnabled,
                    )
                is PaymentMethodsAction.EditMode -> buildModel(isInEditMode = action.isInEditMode)
                is PaymentMethodsAction.PayWithCard -> {
                    buildModel(isLoading = true)
                    val paymentMethod = _uiState.value?.currentPaymentMethod
                    if (paymentMethod is CardPaymentMethodModel) {
                        sendCardPaymentRequest(paymentMethod, action.transactionDetail)
                    }
                }
            }
        }

        private fun payWithSelectedCard(paymentMethod: PaymentMethodModel?) {
            if (paymentMethod is CardPaymentMethodModel) {
                val isSecurityCodeRequired =
                    judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode || judo.uiConfiguration.shouldAskForCSC
                val cardNetwork =
                    if (isSecurityCodeRequired) {
                        val card = paymentMethod.selectedCard
                        card?.network
                    } else {
                        null
                    }
                if (judo.uiConfiguration.shouldAskForBillingInformation ||
                    isSecurityCodeRequired ||
                    judo.uiConfiguration.shouldAskForCardholderName
                ) {
                    val cardEntryOptions =
                        CardEntryOptions(
                            isPresentedFromPaymentMethods = true,
                            cardNetwork = cardNetwork,
                        )
                    _cardEntryEffect.tryEmit(cardEntryOptions)
                } else {
                    sendCardPaymentRequest(paymentMethod, TransactionDetails.Builder())
                }
            }
        }

        @Throws(IllegalStateException::class)
        private fun sendCardPaymentRequest(
            paymentMethod: CardPaymentMethodModel,
            transactionDetailBuilder: TransactionDetails.Builder,
        ) = viewModelScope.launch {
            val card = paymentMethod.selectedCard
            card?.let {
                val entity = cardRepository.findWithId(it.id)

                cardRepository.updateAllLastUsedToFalse()
                cardRepository.insert(entity.apply { isLastUsed = true })

                transactionDetailBuilder
                    .setCardToken(entity.token)
                    .setCardLastFour(entity.ending)
                    .setCardType(entity.network)
                    .setExpirationDate(entity.expireDate)
                if (!judo.uiConfiguration.shouldAskForCardholderName) {
                    transactionDetailBuilder.setCardHolderName(entity.cardholderName)
                }

                val result =
                    if (judo.paymentWidgetType == PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS) {
                        cardTransactionRepository.preAuthWithToken(transactionDetailBuilder.build(), challengeRunner)
                    } else {
                        cardTransactionRepository.paymentWithToken(transactionDetailBuilder.build(), challengeRunner)
                    }

                buildModel()
                _paymentResultEffect.emit(result)
            }
        }

        private fun deleteCardWithId(id: Int) =
            viewModelScope.launch {
                cardRepository.deleteCardWithId(id)
            }

        @Suppress("LongMethod", "CyclomaticComplexMethod")
        private fun buildModel(
            selectedMethod: PaymentMethod = selectedPaymentMethod,
            isLoading: Boolean = false,
            selectedCardId: Int = selectedCardIdentifier,
            isInEditMode: Boolean = false,
        ) {
            val cardModel: CardViewModel

            val recyclerViewData = mutableListOf<PaymentMethodItem>()
            var allMethods = judo.paymentMethods.toList()
            val cards = cardList

            if (allMethods.size > 1) {
                recyclerViewData.add(
                    PaymentMethodSelectorItem(
                        PaymentMethodItemType.SELECTOR,
                        allMethods,
                        selectedMethod,
                    ),
                )
            }

            val method: PaymentMethodModel =
                when (selectedMethod) {
                    PaymentMethod.CARD -> {
                        var selectedCard: PaymentMethodSavedCardItem? = null
                        if (cards.isEmpty()) {
                            // placeholder
                            recyclerViewData.add(
                                PaymentMethodGenericItem(
                                    PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER,
                                    isInEditMode,
                                ),
                            )
                            cardModel = NoPaymentMethodSelectedViewModel()
                        } else {
                            recyclerViewData.add(
                                PaymentMethodGenericItem(
                                    PaymentMethodItemType.SAVED_CARDS_HEADER,
                                    isInEditMode,
                                ),
                            )

                            // cards
                            val defaultSelected = cards.map { it.isDefault }.contains(true)
                            val cardItems =
                                cards.map { entity ->
                                    entity.toPaymentMethodSavedCardItem().apply {
                                        isSelected =
                                            when {
                                                selectedCardId > -1 -> id == selectedCardId
                                                defaultSelected -> entity.isDefault
                                                else -> entity.isLastUsed
                                            }
                                        this.isInEditMode = isInEditMode
                                    }
                                }
                            recyclerViewData.addAll(cardItems)

                            // footer
                            recyclerViewData.add(
                                PaymentMethodGenericItem(
                                    PaymentMethodItemType.SAVED_CARDS_FOOTER,
                                    isInEditMode,
                                ),
                            )

                            selectedCard = cardItems.firstOrNull { it.isSelected } ?: cardItems.first()
                            cardModel = selectedCard.toPaymentCardViewModel()
                        }
                        CardPaymentMethodModel(selectedCard = selectedCard, items = recyclerViewData)
                    }

                    PaymentMethod.GOOGLE_PAY -> {
                        cardModel = GooglePayCardViewModel()
                        GooglePayPaymentMethodModel(items = recyclerViewData)
                    }
                }

            val callToActionModel =
                PaymentCallToActionViewModel(
                    amount = judo.amount.formatted,
                    buttonType = method.type.paymentButtonType,
                    paymentButtonState = buildPaymentButtonState(method.type, isLoading, cardModel),
                    shouldDisplayAmount = judo.uiConfiguration.shouldPaymentMethodsDisplayAmount,
                )

            val headerViewModel = PaymentMethodsHeaderViewModel(cardModel, callToActionModel)
            _uiState.value = PaymentMethodsModel(headerViewModel, method)
        }

        private fun buildPaymentButtonState(
            method: PaymentMethod,
            isLoading: Boolean,
            cardModel: CardViewModel,
        ): ButtonState =
            when (method) {
                PaymentMethod.CARD -> payWithCardButtonState(isLoading, cardModel)
                PaymentMethod.GOOGLE_PAY ->
                    if (isLoading) {
                        ButtonState.Disabled(R.string.jp_empty)
                    } else {
                        ButtonState.Enabled(R.string.jp_empty)
                    }
            }

        private fun payWithCardButtonState(
            isLoading: Boolean,
            cardModel: CardViewModel,
        ): ButtonState =
            when {
                isLoading -> ButtonState.Loading
                cardModel is PaymentCardViewModel &&
                    cardDate
                        .apply {
                            date = cardModel.expireDate
                        }.isAfterToday
                ->
                    ButtonState.Enabled(R.string.jp_pay_now)
                else -> ButtonState.Disabled(R.string.jp_pay_now)
            }
    }
