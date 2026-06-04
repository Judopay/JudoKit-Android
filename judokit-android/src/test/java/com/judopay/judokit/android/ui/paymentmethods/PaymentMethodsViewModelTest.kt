package com.judopay.judokit.android.ui.paymentmethods

import android.app.Application
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.response.CardDate
import com.judopay.judokit.android.collectFlow
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.formatted
import com.judopay.judokit.android.model.paymentButtonType
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemType
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.judokit.android.ui.paymentmethods.model.CardPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

private const val CARD_ID = 1

@ExperimentalCoroutinesApi
@DisplayName("Testing payment methods view model logic")
@Suppress("DEPRECATION")
internal class PaymentMethodsViewModelTest {
    private val testScheduler = StandardTestDispatcher().scheduler
    private val testDispatcher = UnconfinedTestDispatcher(testScheduler)

    private val application: Application = mockk(relaxed = true)
    private val cardDate: CardDate = mockk(relaxed = true)
    private val cardRepository: TokenizedCardRepository = mockk(relaxed = true)
    private val judo: Judo = mockk(relaxed = true)

    private val allCardsFlow = MutableSharedFlow<List<TokenizedCardEntity>>(replay = 1)

    private lateinit var sut: PaymentMethodsViewModel

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic("com.judopay.judokit.android.model.AmountKt")
        mockkStatic("com.judopay.judokit.android.model.PaymentMethodKt")
        mockkStatic("com.judopay.judokit.android.ui.paymentmethods.MappersKt")

        every { judo.paymentMethods } returns arrayOf(PaymentMethod.CARD)
        every { judo.amount.formatted } returns "£1.00"
        every { PaymentMethod.CARD.paymentButtonType } returns mockk(relaxed = true)
        every { cardRepository.allCards } returns allCardsFlow

        allCardsFlow.tryEmit(emptyList())

        sut = PaymentMethodsViewModel(cardDate, cardRepository, application, judo)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    @DisplayName("Given ViewModel is initialized, then uiState is updated")
    fun updateUiStateOnInit() {
        assertNotNull(sut.uiState.value)
    }

    @Test
    @DisplayName("Given allCards flow emits a non-empty list, then uiState reflects the cards")
    fun updateUiStateWhenCardsEmitted() =
        runTest(testScheduler) {
            val card = buildMockedCard()

            allCardsFlow.emit(listOf(card))

            val method = sut.uiState.value?.currentPaymentMethod
            assertTrue(method is CardPaymentMethodModel)
            assertNotNull((method as CardPaymentMethodModel).selectedCard)
        }

    @Test
    @DisplayName("Given DeleteCard is sent, then cardRepository.deleteCardWithId is called with the correct id")
    fun deleteCardOnDeleteCardAction() {
        sut.send(PaymentMethodsAction.DeleteCard(CARD_ID))

        coVerify { cardRepository.deleteCardWithId(CARD_ID) }
    }

    @Test
    @DisplayName("Given SelectStoredCard is sent, then current payment method remains CardPaymentMethodModel")
    fun updateUiStateOnSelectStoredCard() {
        sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))

        assertTrue(sut.uiState.value?.currentPaymentMethod is CardPaymentMethodModel)
    }

    @Test
    @DisplayName("Given EditMode(true) is sent, then current payment method remains CardPaymentMethodModel")
    fun updateUiStateOnEditModeAction() {
        sut.send(PaymentMethodsAction.EditMode(true))

        assertTrue(sut.uiState.value?.currentPaymentMethod is CardPaymentMethodModel)
    }

    @Test
    @DisplayName("Given UpdateButtonState(false) is sent, then payment button state is Loading")
    fun updateUiStateOnUpdateButtonStateAction() {
        sut.send(PaymentMethodsAction.UpdateButtonState(false))

        val buttonState =
            sut.uiState.value
                ?.headerModel
                ?.callToActionModel
                ?.paymentButtonState
        assertEquals(ButtonState.Loading, buttonState)
    }

    @Test
    @DisplayName("Given Update is sent, then payment button state is Disabled")
    fun updateUiStateOnUpdateAction() {
        sut.send(PaymentMethodsAction.Update)

        val buttonState =
            sut.uiState.value
                ?.headerModel
                ?.callToActionModel
                ?.paymentButtonState
        assertEquals(ButtonState.Disabled(R.string.jp_pay_now), buttonState)
    }

    @Test
    @DisplayName("Given SelectPaymentMethod is sent with GOOGLE_PAY, then uiState reflects the new method")
    fun updateUiStateOnSelectPaymentMethodWithGooglePay() {
        every { judo.paymentMethods } returns arrayOf(PaymentMethod.CARD, PaymentMethod.GOOGLE_PAY)
        every { PaymentMethod.GOOGLE_PAY.paymentButtonType } returns mockk(relaxed = true)
        allCardsFlow.tryEmit(emptyList())
        sut = PaymentMethodsViewModel(cardDate, cardRepository, application, judo)

        sut.send(PaymentMethodsAction.SelectPaymentMethod(PaymentMethod.GOOGLE_PAY))

        assertEquals(
            PaymentMethod.GOOGLE_PAY,
            sut.uiState.value
                ?.currentPaymentMethod
                ?.type,
        )
    }

    @Test
    @DisplayName("Given SelectPaymentMethod is sent with the already selected method, then uiState is not changed")
    fun uiStateNotChangedWhenSelectingSamePaymentMethod() {
        val initialState = sut.uiState.value

        sut.send(PaymentMethodsAction.SelectPaymentMethod(PaymentMethod.CARD))

        assertEquals(initialState, sut.uiState.value)
    }

    @Test
    @DisplayName("Given InitiateSelectedCardPayment is sent and billing info is required, then cardEntryEffect is emitted")
    fun emitCardEntryEffectWhenBillingInfoRequired() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns true

            val results = collectFlow(sut.cardEntryEffect)

            allCardsFlow.emit(listOf(buildMockedCard()))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            assertTrue(results.isNotEmpty())
        }

    @Test
    @DisplayName(
        "Given InitiateSelectedCardPayment is sent and no additional info is required, then startTransactionEffect emits PayWithToken",
    )
    fun emitPayWithTokenEffectWhenNoAdditionalInfoRequired() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode } returns false
            every { judo.uiConfiguration.shouldAskForCSC } returns false
            every { judo.uiConfiguration.shouldAskForCardholderName } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            val effects = collectFlow(sut.startTransactionEffect)

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            assertTrue(effects.any { it is StartTransactionEffect.PayWithToken })
        }

    @Test
    @DisplayName("Given PayWithCard is sent with PAYMENT_METHODS type, then startTransactionEffect emits PayWithToken")
    fun emitPayWithTokenEffectOnPayWithCardAction() =
        runTest(testScheduler) {
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            val effects = collectFlow(sut.startTransactionEffect)

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.PayWithCard(TransactionDetails.Builder()))

            assertTrue(effects.any { it is StartTransactionEffect.PayWithToken })
        }

    @Test
    @DisplayName("Given PayWithCard is sent with PRE_AUTH_PAYMENT_METHODS type, then startTransactionEffect emits PreAuthWithToken")
    fun emitPreAuthWithTokenEffectOnPayWithCardAction() =
        runTest(testScheduler) {
            every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            val effects = collectFlow(sut.startTransactionEffect)

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.PayWithCard(TransactionDetails.Builder()))

            assertTrue(effects.any { it is StartTransactionEffect.PreAuthWithToken })
        }

    @Test
    @DisplayName("Given TransactionResult is sent, then paymentResultEffect is emitted")
    fun emitPaymentResultEffectOnTransactionResult() =
        runTest(testScheduler) {
            val expectedResult = JudoPaymentResult.Success(mockk(relaxed = true))
            val results = collectFlow(sut.paymentResultEffect)

            sut.send(PaymentMethodsAction.TransactionResult(expectedResult))

            assertEquals(expectedResult, results[0])
        }

    @Test
    @DisplayName("Given TransactionResult is sent after payment, then paymentResultEffect emits correct result")
    fun emitPaymentResultEffectAfterStartTransactionEffect() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode } returns false
            every { judo.uiConfiguration.shouldAskForCSC } returns false
            every { judo.uiConfiguration.shouldAskForCardholderName } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val expectedResult: JudoPaymentResult = mockk(relaxed = true)

            val results = collectFlow(sut.paymentResultEffect)

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            // Simulate the Fragment callback after the transaction completes
            sut.send(PaymentMethodsAction.TransactionResult(expectedResult))

            assertEquals(expectedResult, results[0])
        }

    @Test
    @DisplayName("Given paymentWithToken completes, then uiState is updated")
    fun updateUiStateAfterCardTransactionResult() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode } returns false
            every { judo.uiConfiguration.shouldAskForCSC } returns false
            every { judo.uiConfiguration.shouldAskForCardholderName } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            assertNotNull(sut.uiState.value)
        }

    @Test
    @DisplayName("Given no saved cards, then payment button is Disabled")
    fun paymentButtonDisabledWhenNoCards() {
        val buttonState =
            sut.uiState.value
                ?.headerModel
                ?.callToActionModel
                ?.paymentButtonState
        assertEquals(ButtonState.Disabled(R.string.jp_pay_now), buttonState)
    }

    @Test
    @DisplayName("Given a valid card (isAfterToday=true), then payment button is Enabled")
    fun paymentButtonEnabledWhenValidCard() =
        runTest(testScheduler) {
            every { cardDate.isAfterToday } returns true

            allCardsFlow.emit(listOf(buildMockedCard()))

            val buttonState =
                sut.uiState.value
                    ?.headerModel
                    ?.callToActionModel
                    ?.paymentButtonState
            assertEquals(ButtonState.Enabled(R.string.jp_pay_now), buttonState)
        }

    @Test
    @DisplayName("Given an expired card (isAfterToday=false), then payment button is Disabled")
    fun paymentButtonDisabledWhenExpiredCard() =
        runTest(testScheduler) {
            every { cardDate.isAfterToday } returns false

            allCardsFlow.emit(listOf(buildMockedCard()))

            val buttonState =
                sut.uiState.value
                    ?.headerModel
                    ?.callToActionModel
                    ?.paymentButtonState
            assertEquals(ButtonState.Disabled(R.string.jp_pay_now), buttonState)
        }

    @Test
    @DisplayName("Given InitiateSelectedCardPayment and billing info required, then button is Loading")
    fun paymentButtonIsLoadingWhenInitiatePaymentWithBillingRequired() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns true

            allCardsFlow.emit(listOf(buildMockedCard()))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            val buttonState =
                sut.uiState.value
                    ?.headerModel
                    ?.callToActionModel
                    ?.paymentButtonState
            assertEquals(ButtonState.Loading, buttonState)
        }

    @Test
    @DisplayName("Given GOOGLE_PAY method and UpdateButtonState(false), then button is Disabled")
    fun googlePayButtonDisabledOnUpdateButtonStateFalse() {
        every { judo.paymentMethods } returns arrayOf(PaymentMethod.CARD, PaymentMethod.GOOGLE_PAY)
        every { PaymentMethod.GOOGLE_PAY.paymentButtonType } returns mockk(relaxed = true)
        allCardsFlow.tryEmit(emptyList())
        sut = PaymentMethodsViewModel(cardDate, cardRepository, application, judo)
        sut.send(PaymentMethodsAction.SelectPaymentMethod(PaymentMethod.GOOGLE_PAY))

        sut.send(PaymentMethodsAction.UpdateButtonState(false))

        val buttonState =
            sut.uiState.value
                ?.headerModel
                ?.callToActionModel
                ?.paymentButtonState
        assertEquals(ButtonState.Disabled(R.string.jp_empty), buttonState)
    }

    @Test
    @DisplayName("Given GOOGLE_PAY method and UpdateButtonState(true), then button is Enabled")
    fun googlePayButtonEnabledOnUpdateButtonStateTrue() {
        every { judo.paymentMethods } returns arrayOf(PaymentMethod.CARD, PaymentMethod.GOOGLE_PAY)
        every { PaymentMethod.GOOGLE_PAY.paymentButtonType } returns mockk(relaxed = true)
        allCardsFlow.tryEmit(emptyList())
        sut = PaymentMethodsViewModel(cardDate, cardRepository, application, judo)
        sut.send(PaymentMethodsAction.SelectPaymentMethod(PaymentMethod.GOOGLE_PAY))

        sut.send(PaymentMethodsAction.UpdateButtonState(true))

        val buttonState =
            sut.uiState.value
                ?.headerModel
                ?.callToActionModel
                ?.paymentButtonState
        assertEquals(ButtonState.Enabled(R.string.jp_empty), buttonState)
    }

    @ParameterizedTest(name = "verifyCode={0}, askCSC={1}, askCardholderName={2}")
    @MethodSource("cardEntryEffectFlagArgs")
    @DisplayName("Given an additional-info flag is true, then cardEntryEffect is emitted")
    fun emitCardEntryEffectWhenAdditionalInfoRequired(
        shouldVerifySecurityCode: Boolean,
        shouldAskForCSC: Boolean,
        shouldAskForCardholderName: Boolean,
    ) = runTest(testScheduler) {
        every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
        every { judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode } returns shouldVerifySecurityCode
        every { judo.uiConfiguration.shouldAskForCSC } returns shouldAskForCSC
        every { judo.uiConfiguration.shouldAskForCardholderName } returns shouldAskForCardholderName

        val results = collectFlow(sut.cardEntryEffect)

        allCardsFlow.emit(listOf(buildMockedCard()))
        sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

        assertTrue(results.isNotEmpty())
    }

    @Test
    @DisplayName("Given cardEntryEffect is emitted, then options have isPresentedFromPaymentMethods=true")
    fun cardEntryEffectHasIsPresentedFromPaymentMethodsTrue() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns true

            val results = collectFlow(sut.cardEntryEffect)

            allCardsFlow.emit(listOf(buildMockedCard()))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            assertTrue(results.last().isPresentedFromPaymentMethods)
        }

    @Test
    @DisplayName("Given sendCardPaymentRequest, then updateAllLastUsedToFalse is called")
    fun updateAllLastUsedToFalseCalledDuringPayment() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode } returns false
            every { judo.uiConfiguration.shouldAskForCSC } returns false
            every { judo.uiConfiguration.shouldAskForCardholderName } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            coVerify { cardRepository.updateAllLastUsedToFalse() }
        }

    @Test
    @DisplayName("Given sendCardPaymentRequest, then cardRepository insert is called")
    fun cardRepositoryInsertCalledDuringPayment() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode } returns false
            every { judo.uiConfiguration.shouldAskForCSC } returns false
            every { judo.uiConfiguration.shouldAskForCardholderName } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            coVerify { cardRepository.insert(any()) }
        }

    @Test
    @DisplayName("Given shouldAskForCardholderName=false, then entity cardholderName is passed in TransactionDetails")
    fun entityCardholderNamePassedInTransactionDetailsWhenNotAsking() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode } returns false
            every { judo.uiConfiguration.shouldAskForCSC } returns false
            every { judo.uiConfiguration.shouldAskForCardholderName } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val card = buildMockedCard()
            every { card.cardholderName } returns "Alice"
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            val effects = collectFlow(sut.startTransactionEffect)

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            val payWithToken = effects.filterIsInstance<StartTransactionEffect.PayWithToken>().last()
            assertEquals("Alice", payWithToken.details.cardHolderName)
        }

    @Test
    @DisplayName("Given shouldAskForCardholderName=true, then entity cardholderName is NOT put in TransactionDetails")
    fun entityCardholderNameNotPassedInTransactionDetailsWhenAsking() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode } returns false
            every { judo.uiConfiguration.shouldAskForCSC } returns false
            every { judo.uiConfiguration.shouldAskForCardholderName } returns true
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val card = buildMockedCard()
            every { card.cardholderName } returns "Alice"
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            val effects = collectFlow(sut.startTransactionEffect)

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.PayWithCard(TransactionDetails.Builder()))

            val payWithToken = effects.filterIsInstance<StartTransactionEffect.PayWithToken>().last()
            assertNotEquals("Alice", payWithToken.details.cardHolderName)
        }

    @Test
    @DisplayName("Given PRE_AUTH_PAYMENT_METHODS and TransactionResult, then paymentResultEffect emits preAuthWithToken result")
    fun preAuthResultEmittedInPaymentResultEffect() =
        runTest(testScheduler) {
            every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS

            val expectedResult = JudoPaymentResult.Success(mockk(relaxed = true))

            val results = collectFlow(sut.paymentResultEffect)

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.PayWithCard(TransactionDetails.Builder()))

            // Simulate the Fragment callback after the transaction completes
            sut.send(PaymentMethodsAction.TransactionResult(expectedResult))

            assertEquals(expectedResult, results.last())
        }

    @Test
    @DisplayName("Given PayWithCard when GOOGLE_PAY is active, then no startTransactionEffect is emitted")
    fun noPaymentEffectWhenPayWithCardAndGooglePayActive() =
        runTest(testScheduler) {
            every { judo.paymentMethods } returns arrayOf(PaymentMethod.CARD, PaymentMethod.GOOGLE_PAY)
            every { PaymentMethod.GOOGLE_PAY.paymentButtonType } returns mockk(relaxed = true)
            allCardsFlow.tryEmit(emptyList())
            sut = PaymentMethodsViewModel(cardDate, cardRepository, application, judo)
            sut.send(PaymentMethodsAction.SelectPaymentMethod(PaymentMethod.GOOGLE_PAY))

            val effects = collectFlow(sut.startTransactionEffect)

            sut.send(PaymentMethodsAction.PayWithCard(TransactionDetails.Builder()))

            assertTrue(effects.isEmpty())
        }

    @Test
    @DisplayName("Given multiple payment methods, then adapter items contain a selector item")
    fun adapterItemsContainSelectorItemForMultipleMethods() {
        every { judo.paymentMethods } returns arrayOf(PaymentMethod.CARD, PaymentMethod.GOOGLE_PAY)
        every { PaymentMethod.GOOGLE_PAY.paymentButtonType } returns mockk(relaxed = true)
        allCardsFlow.tryEmit(emptyList())
        sut = PaymentMethodsViewModel(cardDate, cardRepository, application, judo)

        val items = (sut.uiState.value?.currentPaymentMethod as? CardPaymentMethodModel)?.items
        assertTrue(items?.any { it is PaymentMethodSelectorItem } == true)
    }

    @Test
    @DisplayName("Given EditMode(true) is sent, then SAVED_CARDS_HEADER generic item reports isInEditMode=true")
    fun cardItemsHaveEditModeTrueAfterEditModeAction() =
        runTest(testScheduler) {
            allCardsFlow.emit(listOf(buildMockedCard()))
            sut.send(PaymentMethodsAction.EditMode(true))

            val items = (sut.uiState.value?.currentPaymentMethod as? CardPaymentMethodModel)?.items
            val headerItem =
                items
                    ?.filterIsInstance<PaymentMethodGenericItem>()
                    ?.firstOrNull { it.type == PaymentMethodItemType.SAVED_CARDS_HEADER }
            assertEquals(true, headerItem?.isInEditMode)
        }

    @Test
    @DisplayName("Given SelectStoredCard(id=2), then isSelected=true is assigned to that card's adapter item")
    fun selectStoredCardAssignsIsSelectedToCorrectItem() =
        runTest(testScheduler) {
            val savedCardItem2: PaymentMethodSavedCardItem =
                mockk(relaxed = true) {
                    every { id } returns 2
                    every { expireDate } returns "12/25"
                }
            val cardViewModel2: PaymentCardViewModel = mockk(relaxed = true) { every { expireDate } returns "12/25" }
            val card2: TokenizedCardEntity =
                mockk(relaxed = true) {
                    every { toPaymentMethodSavedCardItem() } returns savedCardItem2
                    every { savedCardItem2.toPaymentCardViewModel() } returns cardViewModel2
                }

            allCardsFlow.emit(listOf(buildMockedCard(1), card2))
            sut.send(PaymentMethodsAction.SelectStoredCard(2))

            verify { savedCardItem2.isSelected = true }
        }

    @Test
    @DisplayName("Given shouldPaymentMethodsDisplayAmount=true, then callToAction reflects shouldDisplayAmount=true")
    fun callToActionReflectsShouldDisplayAmountTrue() {
        every { judo.uiConfiguration.shouldPaymentMethodsDisplayAmount } returns true

        sut.send(PaymentMethodsAction.Update)

        assertTrue(
            sut.uiState.value
                ?.headerModel
                ?.callToActionModel
                ?.shouldDisplayAmount == true,
        )
    }

    @Test
    @DisplayName("Given shouldPaymentMethodsDisplayAmount=false, then callToAction reflects shouldDisplayAmount=false")
    fun callToActionReflectsShouldDisplayAmountFalse() {
        every { judo.uiConfiguration.shouldPaymentMethodsDisplayAmount } returns false

        sut.send(PaymentMethodsAction.Update)

        assertFalse(
            sut.uiState.value
                ?.headerModel
                ?.callToActionModel
                ?.shouldDisplayAmount == true,
        )
    }

    private fun buildMockedCard(id: Int = CARD_ID): TokenizedCardEntity {
        val savedCardItem: PaymentMethodSavedCardItem =
            mockk(relaxed = true) {
                every { this@mockk.id } returns id
                every { expireDate } returns "12/25"
            }
        val cardViewModel: PaymentCardViewModel =
            mockk(relaxed = true) {
                every { expireDate } returns "12/25"
            }
        return mockk(relaxed = true) {
            every { toPaymentMethodSavedCardItem() } returns savedCardItem
            every { savedCardItem.toPaymentCardViewModel() } returns cardViewModel
        }
    }

    companion object {
        @JvmStatic
        fun cardEntryEffectFlagArgs() =
            listOf(
                Arguments.of(true, false, false), // shouldPaymentMethodsVerifySecurityCode
                Arguments.of(false, true, false), // shouldAskForCSC
                Arguments.of(false, false, true), // shouldAskForCardholderName
            )
    }
}
