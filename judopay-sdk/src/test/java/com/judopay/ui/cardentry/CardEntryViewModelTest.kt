package com.judopay.ui.cardentry

import android.app.Application
import androidx.lifecycle.Observer
import com.judopay.InstantExecutorExtension
import com.judopay.Judo
import com.judopay.R
import com.judopay.api.JudoApiService
import com.judopay.api.model.response.CardToken
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.db.repository.TokenizedCardRepository
import com.judopay.model.Amount
import com.judopay.model.Currency
import com.judopay.model.PaymentWidgetType
import com.judopay.model.Reference
import com.judopay.ui.cardentry.components.FormFieldType
import com.judopay.ui.cardentry.components.FormModel
import com.judopay.ui.cardentry.components.InputModel
import com.judopay.ui.common.ButtonState
import com.judopay.ui.paymentmethods.toTokenizedCardEntity
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
@DisplayName("Testing CardEntryViewModel logic")
internal class CardEntryViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private val judo = getJudo()
    private val service: JudoApiService = mockk(relaxed = true)
    private val repository: TokenizedCardRepository = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)

    private val card: CardToken = mockk(relaxed = true)
    private val entity: TokenizedCardEntity = mockk(relaxed = true)
    private val inputModel: InputModel = getInputModel()
    private val judoApiCallResult = JudoApiCallResult.Success(mockk<Receipt>(relaxed = true))

    private val modelMock = spyk<Observer<CardEntryFragmentModel>>()
    private val judoApiCallResultMock = spyk<Observer<JudoApiCallResult<Receipt>?>>()

    private val enabledFields = listOf(
        FormFieldType.NUMBER,
        FormFieldType.HOLDER_NAME,
        FormFieldType.EXPIRATION_DATE,
        FormFieldType.SECURITY_NUMBER
    )

    private lateinit var sut: CardEntryViewModel

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic("com.judopay.ui.paymentmethods.MappersKt")
        coEvery { repository.insert(card.toTokenizedCardEntity(application)) } returns mockk(relaxed = true)

        coEvery {
            service.payment(any()).hint(JudoApiCallResult::class)
        } returns judoApiCallResult

        coEvery {
            service.preAuthPayment(any()).hint(JudoApiCallResult::class)
        } returns judoApiCallResult

        coEvery {
            service.checkCard(any()).hint(JudoApiCallResult::class)
        } returns judoApiCallResult

        coEvery {
            service.saveCard(any()).hint(JudoApiCallResult::class)
        } returns judoApiCallResult

        coEvery {
            service.registerCard(any()).hint(JudoApiCallResult::class)
        } returns judoApiCallResult
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @DisplayName("Given CardEntryViewModel initialises, when isLoading = false and isFormValid = false, then post model with disabled button")
    @Test
    fun postModelOnInitWithDisabledButton() {

        val mockFormModel = FormModel(
            InputModel(),
            enabledFields,
            judo.supportedCardNetworks.toList(),
            ButtonState.Disabled(R.string.pay_now)
        )
        val slots = mutableListOf<CardEntryFragmentModel>()

        sut = CardEntryViewModel(judo, service, repository, application)
        sut.model.observeForever(modelMock)

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[0]

        assertEquals(CardEntryFragmentModel(mockFormModel), formModel)
    }

    @DisplayName("Given send is called with InsertCard action, then should call repository updateAllLastUsedToFalse")
    @Test
    fun callUpdateAllLastUsedToFalseOnSendWithInsertCardAction() {
        sut = CardEntryViewModel(judo, service, repository, application)

        sut.send(CardEntryAction.InsertCard(card))

        coVerify { repository.updateAllLastUsedToFalse() }
    }

    @DisplayName("Given send is called with InsertCard action, then should call repository insert")
    @Test
    fun insertOnSendWithInsertCardAction() {
        sut = CardEntryViewModel(judo, service, repository, application)

        sut.send(CardEntryAction.InsertCard(card))

        coVerify { repository.insert(card.toTokenizedCardEntity(application)) }
    }

    @DisplayName("Given send is called with ValidationPassed action with isLoading = false and isFormValid = true, then should update model with enabled button")
    @Test
    fun postModelOnValidationPassedWithEnabledButton() {
        val mockFormModel = FormModel(
            inputModel,
            enabledFields,
            judo.supportedCardNetworks.toList(),
            ButtonState.Enabled(R.string.pay_now)
        )
        val slots = mutableListOf<CardEntryFragmentModel>()

        sut = CardEntryViewModel(judo, service, repository, application)
        sut.model.observeForever(modelMock)

        sut.send(CardEntryAction.ValidationPassed(inputModel))

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[1]
        assertEquals(CardEntryFragmentModel(mockFormModel), formModel)
    }

    @DisplayName("Given send is called with SubmitForm action with isLoading = true and isFormValid = true, then should update model with loading button")
    @Test
    fun postModelOnSubmitFormWithLoadingButton() {
        val slots = mutableListOf<CardEntryFragmentModel>()

        val myFormModel = FormModel(
            InputModel(),
            enabledFields,
            judo.supportedCardNetworks.toList(),
            ButtonState.Loading
        )

        sut = CardEntryViewModel(judo, service, repository, application)
        sut.model.observeForever(modelMock)

        sut.send(CardEntryAction.SubmitForm)

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[1]
        assertEquals(CardEntryFragmentModel(myFormModel), formModel)
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is card payment, then should invoke service.payment() method")
    @Test
    fun makePaymentRequestOnSubmitFormWithCardPaymentWidgetType() {
        sut = CardEntryViewModel(judo, service, repository, application)

        sut.send(CardEntryAction.ValidationPassed(inputModel))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { service.payment(any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is pre auth card payment, then should invoke service.preAuthPayment() method")
    @Test
    fun makePreAuthPaymentRequestOnSubmitFormWithPreAuthCardPaymentWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_CARD_PAYMENT

        sut = CardEntryViewModel(judo, service, repository, application)

        sut.send(CardEntryAction.ValidationPassed(inputModel))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { service.preAuthPayment(any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is create card token, then should invoke service.registerCard() method")
    @Test
    fun makeRegisterCardRequestOnSubmitFormWithCreateCardTokenWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.CREATE_CARD_TOKEN

        sut = CardEntryViewModel(judo, service, repository, application)

        sut.send(CardEntryAction.ValidationPassed(inputModel))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { service.registerCard(any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is check card, then should invoke service.checkCard() method")
    @Test
    fun makeCheckCardRequestOnSubmitFormWithCheckCardWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.CHECK_CARD

        sut = CardEntryViewModel(judo, service, repository, application)

        sut.send(CardEntryAction.ValidationPassed(inputModel))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { service.checkCard(any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is save card, then should invoke service.saveCard() method")
    @Test
    fun makeSaveCardRequestOnSubmitFormWithSaveCardWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.SAVE_CARD

        sut = CardEntryViewModel(judo, service, repository, application)

        sut.send(CardEntryAction.ValidationPassed(inputModel))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { service.saveCard(any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is GOOGLE_PAY, then should throw IllegalArguementException")
    @Test
    fun throwExceptionOnSubmitFormWithGooglePayWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

        sut = CardEntryViewModel(judo, service, repository, application)

        try {
            sut.send(CardEntryAction.SubmitForm)
        } catch (e: IllegalStateException) {
            assertEquals(IllegalStateException::class.java, e::class.java)
        }
    }

    @DisplayName("Given send is called with SubmitForm action, then update judoApiCallResult model")
    @Test
    fun postJudoApiCallResultOnSubmitForm() {
        val slots = mutableListOf<JudoApiCallResult<Receipt>>()
        every { judo.uiConfiguration.avsEnabled } returns true

        sut = CardEntryViewModel(judo, service, repository, application)
        sut.judoApiCallResult.observeForever(judoApiCallResultMock)

        sut.send(CardEntryAction.ValidationPassed(inputModel))
        sut.send(CardEntryAction.SubmitForm)

        verify { judoApiCallResultMock.onChanged(capture(slots)) }

        val result = slots[0]
        assertEquals(judoApiCallResult, result)
    }

    @DisplayName("Given payment widget type is payment methods, then button text should be add card")
    @Test
    fun givenPaymentWidgetIsPaymentMethodsThenButtonTextShouldBeAddCard() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

        val mockFormModel = FormModel(
            InputModel(),
            enabledFields,
            judo.supportedCardNetworks.toList(),
            ButtonState.Disabled(R.string.add_card)
        )
        val slots = mutableListOf<CardEntryFragmentModel>()

        sut = CardEntryViewModel(judo, service, repository, application)
        sut.model.observeForever(modelMock)

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[0]

        assertEquals(CardEntryFragmentModel(mockFormModel), formModel)
    }

    @DisplayName("Given payment widget type is not supported, then button text should be empty string")
    @Test
    fun givenPaymentWidgetIsUnsupportedThenButtonTextShouldBeEmptyString() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

        val mockFormModel = FormModel(
            InputModel(),
            enabledFields,
            judo.supportedCardNetworks.toList(),
            ButtonState.Disabled(R.string.empty)
        )
        val slots = mutableListOf<CardEntryFragmentModel>()

        sut = CardEntryViewModel(judo, service, repository, application)
        sut.model.observeForever(modelMock)

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[0]

        assertEquals(CardEntryFragmentModel(mockFormModel), formModel)
    }

    private fun getInputModel() = mockk<InputModel>(relaxed = true) {
        every { cardNumber } returns "4111111111111111"
        every { cardHolderName } returns "name"
        every { expirationDate } returns "12/20"
        every { securityNumber } returns "452"
    }

    private fun getJudo() = mockk<Judo>(relaxed = true) {
        every { paymentWidgetType } returns PaymentWidgetType.CARD_PAYMENT
        every { judoId } returns "id"
        every { siteId } returns "siteId"
        every { apiToken } returns "token"
        every { apiSecret } returns "secret"
        every { amount } returns Amount("1", Currency.EUR)
        every { reference } returns Reference("consumer", "payment")
    }
}
