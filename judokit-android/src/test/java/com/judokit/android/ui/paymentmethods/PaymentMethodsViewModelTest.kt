package com.judokit.android.ui.paymentmethods

import android.app.Application
import androidx.lifecycle.Observer
import com.judokit.android.InstantExecutorExtension
import com.judokit.android.Judo
import com.judokit.android.R
import com.judokit.android.api.JudoApiService
import com.judokit.android.api.model.request.BankSaleRequest
import com.judokit.android.api.model.response.BankSaleResponse
import com.judokit.android.api.model.response.CardDate
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.Receipt
import com.judokit.android.db.entity.TokenizedCardEntity
import com.judokit.android.db.repository.TokenizedCardRepository
import com.judokit.android.model.Amount
import com.judokit.android.model.Currency
import com.judokit.android.model.PaymentMethod
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.model.Reference
import com.judokit.android.service.polling.PollingService
import com.judokit.android.ui.common.ButtonState
import com.judokit.android.ui.paymentmethods.adapter.model.IdealBank
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judokit.android.ui.paymentmethods.components.GooglePayCardViewModel
import com.judokit.android.ui.paymentmethods.components.NoPaymentMethodSelectedViewModel
import com.judokit.android.ui.paymentmethods.components.PayByBankCardViewModel
import com.judokit.android.ui.paymentmethods.model.CardPaymentMethodModel
import com.judokit.android.ui.paymentmethods.model.Event
import com.judokit.android.ui.paymentmethods.model.GooglePayPaymentMethodModel
import com.judokit.android.ui.paymentmethods.model.IdealPaymentCardViewModel
import com.judokit.android.ui.paymentmethods.model.IdealPaymentMethodModel
import com.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import com.zapp.library.merchant.util.PBBAAppUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
@DisplayName("Testing PaymentMethodsViewModel logic")
internal class PaymentMethodsViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private val cardDate: CardDate = mockk(relaxed = true)
    private val service: JudoApiService = mockk(relaxed = true)
    private val pollingService: PollingService = mockk(relaxed = true)
    private val repository: TokenizedCardRepository = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private val judo = getJudo()

    private lateinit var sut: PaymentMethodsViewModel

    private val paymentMethodsModel = spyk<Observer<PaymentMethodsModel>>()
    private val judoApiCallResult = spyk<Observer<JudoApiCallResult<Receipt>>>()
    private val payWithIdealObserver = spyk<Observer<Event<String>>>()
    private val payByBankResult = spyk<Observer<JudoApiCallResult<BankSaleResponse>>>()

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic("com.zapp.library.merchant.util.PBBAAppUtils")
        every { PBBAAppUtils.isCFIAppAvailable(application) } returns false
        coEvery { repository.findWithId(0) } returns mockk(relaxed = true)
        coEvery { repository.allCardsSync.value } returns null
        coEvery {
            service.tokenPayment(any()).hint(JudoApiCallResult::class)
        } returns mockk(relaxed = true)
        coEvery { service.sale(any<BankSaleRequest>()) } returns mockk(relaxed = true)

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @DisplayName("Given PaymentMethodsViewModel has initialised, when currency is not EUR, then call buildModel() and update PaymentMethodsModel without iDEAL")
    @Test
    fun removeIdealFromModelWhenNotEur() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertFalse(
            (model.currentPaymentMethod.items[0] as PaymentMethodSelectorItem).paymentMethods.contains(
                PaymentMethod.IDEAL
            )
        )
    }

    @DisplayName("Given PaymentMethodsViewModel has initialised, when currency is EUR, then call buildModel() and update PaymentMethodsModel with iDEAL")
    @Test
    fun addIdealToModelWhenEur() {
        val judo = getJudo().apply { every { amount } returns Amount("1", Currency.EUR) }
        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertTrue(
            (model.currentPaymentMethod.items[0] as PaymentMethodSelectorItem).paymentMethods.containsAll(
                judo.paymentMethods.toList().filter { it != PaymentMethod.PAY_BY_BANK }
            )
        )
    }

    @DisplayName("Given PaymentMethodsViewModel has initialised, when there is more than one payment method, then call buildModel() and update PaymentMethodsModel with PaymentMethodSelectorItem")
    @Test
    fun updateModelWithSelectorItem() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertTrue(model.currentPaymentMethod.items[0] is PaymentMethodSelectorItem)
    }

    @DisplayName("Given PaymentMethodsViewModel has initialised, when there is one payment method, then call buildModel() and update PaymentMethodsModel without PaymentMethodSelectorItem")
    @Test
    fun updateModelWithoutSelectorItem() {
        val judo = getJudo().apply { every { paymentMethods } returns arrayOf(PaymentMethod.CARD) }

        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertFalse(model.currentPaymentMethod.items[0] is PaymentMethodSelectorItem)
    }

    @DisplayName("Given selected method is cards, then update PaymentMethodsModel currentSelected field to cards")
    @Test
    fun updateCurrentSelectedFieldToCards() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertTrue((model.currentPaymentMethod.items[0] as PaymentMethodSelectorItem).currentSelected == PaymentMethod.CARD)
    }

    @DisplayName("Given selected method is cards, when no cards are saved, then add placeholder")
    @Test
    fun addPlaceholderWithNoCardsSaved() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertTrue(model.headerModel.cardModel is NoPaymentMethodSelectedViewModel)
    }

    @DisplayName("Given selected method is cards, when a card is saved, then add PaymentCardViewModel header")
    @Test
    fun addSavedCardsHeaderWithCardsStored() {
        every { repository.allCardsSync.value } returns listOf(mockk(relaxed = true))

        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertTrue(model.headerModel.cardModel is PaymentCardViewModel)
    }

    @DisplayName("Given selected method is cards, when selected card is not expired, then set button state to Enabled")
    @Test
    fun setButtonStateToEnabledWithCardNotExpired() {
        every { repository.allCardsSync.value } returns listOf(mockk(relaxed = true))
        every { cardDate.isAfterToday } returns true

        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertEquals(
            model.headerModel.callToActionModel.paymentButtonState,
            ButtonState.Enabled(R.string.pay_now)
        )
    }

    @DisplayName("Given one of the stored cards isDefault=true, then that card is selected")
    @Test
    fun selectDefaultCard() {
        val card = mockk<TokenizedCardEntity>(relaxed = true) { every { isDefault } returns true }
        every { repository.allCardsSync.value } returns listOf(card)

        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertTrue((model.currentPaymentMethod as CardPaymentMethodModel).selectedCard!!.isSelected)
    }

    @DisplayName("Given selected method is google pay, then add GooglePayCardViewModel")
    @Test
    fun addGooglePayCardViewModelOnGooglePaySelected() {
        val slots = mutableListOf<PaymentMethodsModel>()

        val judo =
            getJudo().apply { every { paymentMethods } returns arrayOf(PaymentMethod.GOOGLE_PAY) }

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertTrue(model.headerModel.cardModel is GooglePayCardViewModel)
    }

    @DisplayName("Given selected method is iDEAL, then add IdealPaymentCardViewModel")
    @Test
    fun addIdealPaymentCardViewModelOnIdealSelected() {
        val slots = mutableListOf<PaymentMethodsModel>()

        val judo = getJudo().apply { every { paymentMethods } returns arrayOf(PaymentMethod.IDEAL) }

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[0]
        assertTrue(model.headerModel.cardModel is IdealPaymentCardViewModel)
    }

    @DisplayName("Given send with DeleteCard action is called, then delete card by id")
    @Test
    fun deleteCardByIdOnDeleteCardAction() {
        sut.send(PaymentMethodsAction.DeleteCard(1))

        coVerify { repository.deleteCardWithId(1) }
    }

    @DisplayName("Given send with DeleteCard action is called, then build model")
    @Test
    fun buildModelOnDeleteCardAction() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.send(PaymentMethodsAction.DeleteCard(1))

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
    }

    @DisplayName("Given send with PayWithSelectedStoredCard action is called, then build paymentMethodModel with isLoading=true")
    @Test
    fun updatePaymentMethodModelWithIsLoadingTrueOnPayWithSelectedCStoredCard() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertTrue(model.headerModel.callToActionModel.paymentButtonState is ButtonState.Loading)
    }

    @DisplayName("Given send with PayWithSelectedStoredCard action is called, when currentPaymentMethod is CardPaymentMethodModel, then call repository.findWithId(id)")
    @Test
    fun callFindWithIdOnPayWithSelectedStoredCardWithCardPaymentMethodModel() {
        val card = mockk<TokenizedCardEntity>(relaxed = true) { every { isDefault } returns true }
        every { repository.allCardsSync.value } returns listOf(card)

        sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)

        coVerify { repository.findWithId(0) }
    }

    @DisplayName("Given send with PayWithSelectedStoredCard action is called, when PaymentWidgetType is SERVER_TO_SERVER, then update judoApiCallResult without api call")
    @Test
    fun updateJudoApiCallResultOnPaymentWidgetTypeServerToServer() {
        val judo = getJudo().apply {
            every { paymentWidgetType } returns PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS
            every { judoId } returns "1"
        }
        val card = mockk<TokenizedCardEntity>(relaxed = true) { every { isDefault } returns true }
        every { repository.allCardsSync.value } returns listOf(card)

        val slots = mutableListOf<JudoApiCallResult<Receipt>>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.judoApiCallResult.observeForever(judoApiCallResult)

        sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)

        verify { judoApiCallResult.onChanged(capture(slots)) }
    }

    @DisplayName("Given send with PayWithSelectedStoredCard action is called, when PaymentWidgetType is PAYMENT_METHODS, then call service.tokenPayment(request)")
    @Test
    fun callTokenPaymentOnPayWithSelectedStoredCardWithPaymentWidgetTypePaymentMethods() {
        val card = mockk<TokenizedCardEntity>(relaxed = true) {
            every { id } returns 1
            every { token } returns "token"
            every { isDefault } returns true
        }
        coEvery { repository.findWithId(1) } returns card
        every { repository.allCardsSync.value } returns listOf(card)

        sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)

        coVerify { service.tokenPayment(any()) }
    }

    @DisplayName("Given send with PayWithSelectedStoredCard action is called, when PaymentWidgetType is PRE_AUTH_PAYMENT_METHODS, then call service.preAuthTokenPayment(request)")
    @Test
    fun callPreAuthTokenPaymentOnPayWithSelectedStoredCardWithPaymentWidgetTypePreAuthPaymentMethods() {
        val judo =
            getJudo().apply { every { paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS }
        val card = mockk<TokenizedCardEntity>(relaxed = true) {
            every { id } returns 1
            every { token } returns "token"
            every { isDefault } returns true
        }
        coEvery { repository.findWithId(1) } returns card
        every { repository.allCardsSync.value } returns listOf(card)

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)

        coVerify { service.preAuthTokenPayment(any()) }
    }

    @DisplayName("Given send with PayWithSelectedStoredCard action is called, when PaymentWidgetType is CARD_PAYMENT, then throw IllegalStateException")
    @Test
    fun throwIllegalStateExceptionOnPayWithSelectedStoredCardWithPaymentWidgetTypeCardPayment() {
        val judo =
            getJudo().apply { every { paymentWidgetType } returns PaymentWidgetType.CARD_PAYMENT }
        val card = mockk<TokenizedCardEntity>(relaxed = true) {
            every { id } returns 1
            every { token } returns "token"
            every { isDefault } returns true
        }
        coEvery { repository.findWithId(1) } returns card
        every { repository.allCardsSync.value } returns listOf(card)

        try {
            sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)
        } catch (e: IllegalStateException) {
            assertEquals(IllegalStateException::class.java, e::class.java)
        }
    }

    @DisplayName("Given send with PayWithSelectedStoredCard action is called, then call repository.updateAllLastUsedToFalse()")
    @Test
    fun updateAllLastUsedToFalseOnPayWithSelectedStoredCard() {
        val card = mockk<TokenizedCardEntity>(relaxed = true) {
            every { id } returns 1
            every { token } returns "token"
            every { isDefault } returns true
        }
        coEvery { repository.findWithId(1) } returns card
        every { repository.allCardsSync.value } returns listOf(card)

        sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)

        coVerify { repository.updateAllLastUsedToFalse() }
    }

    @DisplayName("Given send with PayWithSelectedStoredCard action is called, then call repository.insert(entity)")
    @Test
    fun insertCardOnPayWithSelectedStoredCard() {
        val card = mockk<TokenizedCardEntity>(relaxed = true) {
            every { id } returns 1
            every { token } returns "token"
            every { isDefault } returns true
            every { isLastUsed } returns true
        }
        coEvery { repository.findWithId(1) } returns card
        every { repository.allCardsSync.value } returns listOf(card)

        sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)

        coVerify { repository.insert(card) }
    }

    @DisplayName("Given send with PayWithSelectedIdealBank action is called, then build paymentMethodModel with isLoading=true")
    @Test
    fun updatePaymentMethodModelWithIsLoadingTrueOnPayWithSelectedIdealBank() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.PayWithSelectedIdealBank)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertTrue(model.headerModel.callToActionModel.paymentButtonState is ButtonState.Loading)
    }

    @DisplayName("Given send with PayWithSelectedIdealBank action is called, when current payment method is ideal, then update payWithIdealObserver")
    @Test
    fun updatePayWithIdealObserverOnPayWithSelectedIdealBank() {
        val judo = getJudo().apply { every { paymentMethods } returns arrayOf(PaymentMethod.IDEAL) }
        val slots = mutableListOf<Event<String>>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.payWithIdealObserver.observeForever(payWithIdealObserver)

        sut.send(PaymentMethodsAction.PayWithSelectedIdealBank)

        verify { payWithIdealObserver.onChanged(capture(slots)) }
    }

    @DisplayName("Given send with SelectStoredCard action is called, then build paymentMethodModel with isLoading=false and selectedCardId value")
    @Test
    fun updatePaymentMethodModelWithIsLoadingFalseAndSelectedCardIdValueOnSelectStoredCard() {
        val card = mockk<TokenizedCardEntity>(relaxed = true) {
            every { id } returns 1
            every { isDefault } returns true
        }
        every { repository.allCardsSync.value } returns listOf(card)

        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.SelectStoredCard(1))

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertFalse(model.headerModel.callToActionModel.paymentButtonState is ButtonState.Loading)
        assertTrue((model.currentPaymentMethod as CardPaymentMethodModel).selectedCard!!.id == 1)
    }

    @DisplayName("Given send with SelectIdealBank action is called, then build paymentMethodModel with isLoading=false and selectedBank value")
    @Test
    fun updatePaymentMethodModelWithIsLoadingFalseAndSelectedBankIdValueOnSelectIdealBank() {
        val judo = getJudo().apply { every { paymentMethods } returns arrayOf(PaymentMethod.IDEAL) }

        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.SelectIdealBank(IdealBank.ING_BANK))

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertFalse(model.headerModel.callToActionModel.paymentButtonState is ButtonState.Loading)
        assertTrue((model.currentPaymentMethod as IdealPaymentMethodModel).selectedBank == IdealBank.ING_BANK)
    }

    @DisplayName("Given send with Update action is called, then build paymentMethodModel")
    @Test
    fun updatePaymentMethodModelOnUpdate() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.Update)

        verify(exactly = 2) { paymentMethodsModel.onChanged(capture(slots)) }
    }

    @DisplayName("Given send with SelectPaymentMethod action is called with selected method, then build paymentMethodModel with GooglePayPaymentMethodModel")
    @Test
    fun updatePaymentMethodWithGooglePayMethodModelOnSelectPaymentMethod() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.SelectPaymentMethod(PaymentMethod.GOOGLE_PAY))

        verify(exactly = 2) { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertTrue(model.currentPaymentMethod is GooglePayPaymentMethodModel)
    }

    @DisplayName("Given send with SelectPaymentMethod action is called with selected method, when it is already selected, then do not build paymentMethodModel")
    @Test
    fun doNotUpdatePaymentMethodOnSelectPaymentMethodWhenAlreadySelected() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.SelectPaymentMethod(PaymentMethod.CARD))

        verify(exactly = 1) { paymentMethodsModel.onChanged(capture(slots)) }
    }

    @DisplayName("Given send with UpdatePayWithGooglePayButtonState action is called with buttonEnabled=false, then build paymentMethodModel with button state Disabled")
    @Test
    fun updatePaymentMethodWithButtonStateDisabledOnUpdatePayWithGooglePayButtonStateWithButtonEnabledFalse() {
        val judo =
            getJudo().apply { every { paymentMethods } returns arrayOf(PaymentMethod.GOOGLE_PAY) }

        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.UpdateButtonState(false))

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertTrue(model.headerModel.callToActionModel.paymentButtonState is ButtonState.Disabled)
    }

    @DisplayName("Given send with UpdatePayWithGooglePayButtonState action is called with buttonEnabled=true, then build paymentMethodModel with button state Enabled")
    @Test
    fun updatePaymentMethodWithButtonStateEnabledOnUpdatePayWithGooglePayButtonStateWithButtonEnabledTrue() {
        val judo =
            getJudo().apply { every { paymentMethods } returns arrayOf(PaymentMethod.GOOGLE_PAY) }

        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(
            cardDate,
            repository,
            service,
            application,
            judo
        )

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.UpdateButtonState(true))

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertTrue(model.headerModel.callToActionModel.paymentButtonState is ButtonState.Enabled)
    }

    @DisplayName("Given send with EditMode action is called with isInEditMode=true, then build paymentMethodModel with isInEditMode=true")
    @Test
    fun updatePaymentMethodModelWithIsInEditModeTrueOnEditModeWithEditModeTrue() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.EditMode(true))

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertTrue((model.currentPaymentMethod.items[1] as PaymentMethodGenericItem).isInEditMode)
    }

    @DisplayName("Given send with EditMode action is called with isInEditMode=false, then build paymentMethodModel with isInEditMode=false")
    @Test
    fun updatePaymentMethodModelWithIsInEditModeFalseOnEditModeWithEditModeFalse() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.EditMode(false))

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertFalse((model.currentPaymentMethod.items[1] as PaymentMethodGenericItem).isInEditMode)
    }

    @DisplayName("Given send with SelectPaymentMethod action is called, when parameter is PAY_BY_BANK, then build paymentMethodModel with PayByBankCardViewModel")
    @Test
    fun updatePaymentMethodModelOnPayByBankSelected() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut.model.observeForever(paymentMethodsModel)

        sut.send(PaymentMethodsAction.SelectPaymentMethod(PaymentMethod.PAY_BY_BANK))

        verify { paymentMethodsModel.onChanged(capture(slots)) }
        val model = slots[1]
        assertTrue(model.headerModel.cardModel is PayByBankCardViewModel)
    }

//    @DisplayName("Given send with PayWithPayByBank action is called, then make service.sale call")
//    @Test
//    fun updatePaymentMethodModelOnPayWithPayByBank() {
//        sut.model.observeForever(paymentMethodsModel)
//
//        sut.send(PaymentMethodsAction.PayWithPayByBank)
//
//        coVerify { service.sale(any<BankSaleRequest>()) }
//    }
//
//    @DisplayName("Given send with PayWithPayByBank action is called, then update payByBankResult")
//    @Test
//    fun updatePayByBankResultOnPayWithPayByBankAction() {
//        val slots = mutableListOf<JudoApiCallResult<BankSaleResponse>>()
//
//        sut.payWithPayByBankObserver.observeForever(payByBankResult)
//
//        sut.send(PaymentMethodsAction.PayWithPayByBank)
//
//        verify { payByBankResult.onChanged(capture(slots)) }
//    }
//
//    @DisplayName("Given send with StartBankPayment action is called, then start polling")
//    @Test
//    fun startPollingOnStartBankPaymentCall() {
//        sut.send(PaymentMethodsAction.StartBankPayment("orderId"))
//
//        coVerify { pollingService.start() }
//    }
//
//    @DisplayName("Given send with CancelBankPayment action is called, then cancel polling")
//    @Test
//    fun cancelPollingOnCancelBankPaymentCall() {
//        sut.send(PaymentMethodsAction.CancelBankPayment)
//
//        verify { pollingService.cancel() }
//    }
//
//    @DisplayName("Given send with ResetBankPolling action is called, then reset polling")
//    @Test
//    fun resetPollingOnResetBankPollingCall() {
//        sut.send(PaymentMethodsAction.ResetBankPolling)
//
//        verify { pollingService.reset() }
//    }
//
//    @DisplayName("Given send with RetryBankPolling action is called, then retry polling")
//    @Test
//    fun retryPollingOnRetryBankPollingCall() {
//        sut.send(PaymentMethodsAction.RetryBankPolling)
//
//        coVerify { pollingService.retry() }
//    }

    private fun getJudo() = mockk<Judo>(relaxed = true) {
        every { paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS
        every { paymentMethods } returns PaymentMethod.values()
        every { judoId } returns "1"
        every { siteId } returns "siteId"
        every { apiToken } returns "token"
        every { apiSecret } returns "secret"
        every { amount } returns Amount("1", Currency.GBP)
        every { reference } returns Reference("consumer", "payment")
    }
}
