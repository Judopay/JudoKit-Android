package com.judopay.judokit.android.ui.paymentmethods

import android.app.Application
import com.judopay.judokit.android.Judo
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
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.judokit.android.ui.paymentmethods.model.CardPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private const val CARD_ID = 1

@ExperimentalCoroutinesApi
@DisplayName("Testing payment methods view model logic")
internal class PaymentMethodsViewModelTest {
    private val testScheduler = StandardTestDispatcher().scheduler
    private val testDispatcher = UnconfinedTestDispatcher(testScheduler)

    private val application: Application = mockk(relaxed = true)
    private val cardDate: CardDate = mockk(relaxed = true)
    private val cardRepository: TokenizedCardRepository = mockk(relaxed = true)
    private val cardTransactionRepository: CardTransactionRepository = mockk(relaxed = true)
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

        coEvery { cardTransactionRepository.paymentWithToken(any(), any()) } returns JudoPaymentResult.Success(mockk(relaxed = true))
        coEvery { cardTransactionRepository.preAuthWithToken(any(), any()) } returns JudoPaymentResult.Success(mockk(relaxed = true))

        allCardsFlow.tryEmit(emptyList())

        sut = PaymentMethodsViewModel(cardDate, cardRepository, cardTransactionRepository, application, judo)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
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
    @DisplayName("Given SelectStoredCard is sent, then uiState is updated")
    fun updateUiStateOnSelectStoredCard() {
        sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))

        assertNotNull(sut.uiState.value)
    }

    @Test
    @DisplayName("Given EditMode(true) is sent, then uiState is updated")
    fun updateUiStateOnEditModeAction() {
        sut.send(PaymentMethodsAction.EditMode(true))

        assertNotNull(sut.uiState.value)
    }

    @Test
    @DisplayName("Given UpdateButtonState(false) is sent, then uiState is updated")
    fun updateUiStateOnUpdateButtonStateAction() {
        sut.send(PaymentMethodsAction.UpdateButtonState(false))

        assertNotNull(sut.uiState.value)
    }

    @Test
    @DisplayName("Given Update is sent, then uiState is updated")
    fun updateUiStateOnUpdateAction() {
        sut.send(PaymentMethodsAction.Update)

        assertNotNull(sut.uiState.value)
    }

    @Test
    @DisplayName("Given SelectPaymentMethod is sent with GOOGLE_PAY, then uiState reflects the new method")
    fun updateUiStateOnSelectPaymentMethodWithGooglePay() {
        every { judo.paymentMethods } returns arrayOf(PaymentMethod.CARD, PaymentMethod.GOOGLE_PAY)
        every { PaymentMethod.GOOGLE_PAY.paymentButtonType } returns mockk(relaxed = true)
        allCardsFlow.tryEmit(emptyList())
        sut = PaymentMethodsViewModel(cardDate, cardRepository, cardTransactionRepository, application, judo)

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

            val results = mutableListOf<Any>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.cardEntryEffect.collect(results::add)
            }

            allCardsFlow.emit(listOf(buildMockedCard()))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

            assertTrue(results.isNotEmpty())
        }

    @Test
    @DisplayName("Given InitiateSelectedCardPayment is sent and no additional info is required, then paymentWithToken is called")
    fun callPaymentWithTokenWhenNoAdditionalInfoRequired() =
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

            coVerify { cardTransactionRepository.paymentWithToken(any(), any()) }
        }

    @Test
    @DisplayName("Given PayWithCard is sent with PAYMENT_METHODS type, then paymentWithToken is called")
    fun callPaymentWithTokenOnPayWithCardAction() =
        runTest(testScheduler) {
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.PayWithCard(TransactionDetails.Builder()))

            coVerify { cardTransactionRepository.paymentWithToken(any(), any()) }
        }

    @Test
    @DisplayName("Given PayWithCard is sent with PRE_AUTH_PAYMENT_METHODS type, then preAuthWithToken is called")
    fun callPreAuthWithTokenOnPayWithCardAction() =
        runTest(testScheduler) {
            every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.PayWithCard(TransactionDetails.Builder()))

            coVerify { cardTransactionRepository.preAuthWithToken(any(), any()) }
        }

    @Test
    @DisplayName("Given paymentWithToken completes, then paymentResultEffect is emitted")
    fun emitPaymentResultEffectOnCardTransactionResult() =
        runTest(testScheduler) {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode } returns false
            every { judo.uiConfiguration.shouldAskForCSC } returns false
            every { judo.uiConfiguration.shouldAskForCardholderName } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            val expectedResult: JudoPaymentResult = mockk(relaxed = true)
            coEvery { cardTransactionRepository.paymentWithToken(any(), any()) } returns expectedResult

            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(results::add)
            }

            val card = buildMockedCard()
            coEvery { cardRepository.findWithId(CARD_ID) } returns card

            allCardsFlow.emit(listOf(card))
            sut.send(PaymentMethodsAction.SelectStoredCard(CARD_ID))
            sut.send(PaymentMethodsAction.InitiateSelectedCardPayment)

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

    private fun buildMockedCard(): TokenizedCardEntity {
        val savedCardItem: PaymentMethodSavedCardItem =
            mockk(relaxed = true) {
                every { id } returns CARD_ID
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
}
