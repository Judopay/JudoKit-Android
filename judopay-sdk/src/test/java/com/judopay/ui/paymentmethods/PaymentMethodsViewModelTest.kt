package com.judopay.ui.paymentmethods

import android.app.Application
import androidx.lifecycle.Observer
import com.judopay.InstantExecutorExtension
import com.judopay.Judo
import com.judopay.R
import com.judopay.api.JudoApiService
import com.judopay.api.model.response.CardDate
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.db.repository.TokenizedCardRepository
import com.judopay.model.Amount
import com.judopay.model.Currency
import com.judopay.model.PaymentMethod
import com.judopay.model.PaymentWidgetType
import com.judopay.model.Reference
import com.judopay.ui.common.ButtonState
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.ui.paymentmethods.components.GooglePayCardViewModel
import com.judopay.ui.paymentmethods.components.NoPaymentMethodSelectedViewModel
import com.judopay.ui.paymentmethods.model.CardPaymentMethodModel
import com.judopay.ui.paymentmethods.model.IdealPaymentCardViewModel
import com.judopay.ui.paymentmethods.model.PaymentCardViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
    private val repository: TokenizedCardRepository = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private val judo = getJudo()

    private lateinit var sut: PaymentMethodsViewModel

    private val paymentMethodsModel = spyk<Observer<PaymentMethodsModel>>()

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { repository.allCardsSync.value } returns null
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

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

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

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }

        val model = slots[0]

        assertTrue(
            (model.currentPaymentMethod.items[0] as PaymentMethodSelectorItem).paymentMethods.containsAll(
                judo.paymentMethods.toList()
            )
        )
    }

    @DisplayName("Given PaymentMethodsViewModel has initialised, when there is more than one payment method, then call buildModel() and update PaymentMethodsModel with PaymentMethodSelectorItem")
    @Test
    fun updateModelWithSelectorItem() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

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

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }

        val model = slots[0]

        assertFalse(model.currentPaymentMethod.items[0] is PaymentMethodSelectorItem)
    }


    @DisplayName("Given selected method is cards, then update PaymentMethodsModel currentSelected field to cards")
    @Test
    fun updateCurrentSelectedFieldToCards() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }

        val model = slots[0]

        assertTrue((model.currentPaymentMethod.items[0] as PaymentMethodSelectorItem).currentSelected == PaymentMethod.CARD)
    }

    @DisplayName("Given selected method is cards, when no cards are saved, then add placeholder")
    @Test
    fun addPlaceholderWithNoCardsSaved() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

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

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

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

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

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

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

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

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

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

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }

        val model = slots[0]

        assertTrue(model.headerModel.cardModel is IdealPaymentCardViewModel)
    }

    @DisplayName("Given send with DeleteCard action is called, then delete card by id")
    @Test
    fun deleteCardByIdOnDeleteCardAction() {
        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

        sut.send(PaymentMethodsAction.DeleteCard(1))

        coVerify { repository.deleteCardWithId(1) }
    }

    @DisplayName("Given send with DeleteCard action is called, then build model")
    @Test
    fun buildModelOnDeleteCardAction() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

        sut.send(PaymentMethodsAction.DeleteCard(1))

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }
    }

    @DisplayName("Given send with PayWithSelectedStoredCard action is called, then build paymentMethodModel with isLoading=true")
    @Test
    fun updatePaymentMethodModelWithIsLoadingTrueOnPayWithSelectedCStoredCard() {
        val slots = mutableListOf<PaymentMethodsModel>()

        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)

        sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)

        sut.model.observeForever(paymentMethodsModel)

        verify { paymentMethodsModel.onChanged(capture(slots)) }

        val model = slots[0]
        assertTrue(model.headerModel.callToActionModel.paymentButtonState is ButtonState.Loading)
    }

//    @DisplayName("Given send with PayWithSelectedStoredCard action is called, then build paymentMethodModel with isLoading=true")
//    @Test
//    fun updatePaymentMethodModelWithIsLoadingTrueOnPayWithSelectedCStoredCard() {
//        val slots = mutableListOf<PaymentMethodsModel>()
//
//        sut = PaymentMethodsViewModel(cardDate, repository, service, application, judo)
//
//        sut.send(PaymentMethodsAction.PayWithSelectedStoredCard)
//
//        sut.model.observeForever(paymentMethodsModel)
//
//        verify { paymentMethodsModel.onChanged(capture(slots)) }
//
//        val model = slots[0]
//        assertTrue(model.headerModel.callToActionModel.paymentButtonState is ButtonState.Loading)
//    }

    private fun getJudo() = mockk<Judo>(relaxed = true) {
        every { paymentWidgetType } returns PaymentWidgetType.CARD_PAYMENT
        every { paymentMethods } returns PaymentMethod.values()
        every { judoId } returns "id"
        every { siteId } returns "siteId"
        every { apiToken } returns "token"
        every { apiSecret } returns "secret"
        every { amount } returns Amount("1", Currency.GBP)
        every { reference } returns Reference("consumer", "payment")
    }
}