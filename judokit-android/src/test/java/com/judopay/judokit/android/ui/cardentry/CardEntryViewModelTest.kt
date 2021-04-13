package com.judopay.judokit.android.ui.cardentry

import android.app.Application
import androidx.lifecycle.Observer
import com.judopay.judokit.android.InstantExecutorExtension
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.CardScanningResult
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.Reference
import com.judopay.judokit.android.model.formatted
import com.judopay.judokit.android.model.toInputModel
import com.judopay.judokit.android.service.CardTransactionCallback
import com.judopay.judokit.android.service.CardTransactionService
import com.judopay.judokit.android.ui.cardentry.model.FormFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormModel
import com.judopay.judokit.android.ui.cardentry.model.InputModel
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.common.isDependencyPresent
import com.judopay.judokit.android.ui.paymentmethods.toTokenizedCardEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
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
@DisplayName("Testing CardEntryViewModel logic")
internal class CardEntryViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private val judo = getJudo()
    private val repository: TokenizedCardRepository = mockk(relaxed = true)
    private val cardTransactionService: CardTransactionService = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)

    private val card: CardToken = mockk(relaxed = true)
    private var selectedCardNetwork: CardNetwork? = null
    private val inputModel: InputModel = getInputModel()
    private val expectedJudoPaymentResult = JudoPaymentResult.Success(mockk(relaxed = true))

    private val cardTransactionCallback = slot<CardTransactionCallback>()
    private val modelMock = spyk<Observer<CardEntryFragmentModel>>()
    private val judoPaymentResultMock = spyk<Observer<JudoPaymentResult?>>()
    private val securityCodeResultMock = spyk<Observer<String>>()

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

        mockkStatic("retrofit2.KotlinExtensions")
        mockkStatic("com.judopay.judokit.android.ui.paymentmethods.MappersKt")
        mockkStatic("com.judopay.judokit.android.ui.common.FunctionsKt")
        coEvery { repository.insert(card.toTokenizedCardEntity(application)) } returns mockk(relaxed = true)
        coEvery { cardTransactionService.makeTransaction(any(), capture(cardTransactionCallback)) } coAnswers { cardTransactionCallback }
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

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.model.observeForever(modelMock)

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[0]

        assertEquals(CardEntryFragmentModel(mockFormModel), formModel)
    }

    @DisplayName("Given send is called with InsertCard action, then should call repository updateAllLastUsedToFalse")
    @Test
    fun callUpdateAllLastUsedToFalseOnSendWithInsertCardAction() {
        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        sut.send(CardEntryAction.InsertCard(card))

        coVerify { repository.updateAllLastUsedToFalse() }
    }

    @DisplayName("Given send is called with InsertCard action, then should call repository insert")
    @Test
    fun insertOnSendWithInsertCardAction() {
        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

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

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.model.observeForever(modelMock)

        sut.send(CardEntryAction.ValidationStatusChanged(inputModel, true))

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

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.model.observeForever(modelMock)

        sut.send(CardEntryAction.SubmitForm)

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[1]
        assertEquals(CardEntryFragmentModel(myFormModel), formModel)
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is card payment, then should invoke service.payment() method")
    @Test
    fun makePaymentRequestOnSubmitFormWithCardPaymentWidgetType() {
        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        sut.send(CardEntryAction.ValidationStatusChanged(inputModel, true))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { cardTransactionService.makeTransaction(any(), any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is pre auth card payment, then should invoke service.preAuthPayment() method")
    @Test
    fun makePreAuthPaymentRequestOnSubmitFormWithPreAuthCardPaymentWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        sut.send(CardEntryAction.ValidationStatusChanged(inputModel, true))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { cardTransactionService.makeTransaction(any(), any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is create card token, then should invoke service.registerCard() method")
    @Test
    fun makeRegisterCardRequestOnSubmitFormWithCreateCardTokenWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.REGISTER_CARD

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        sut.send(CardEntryAction.ValidationStatusChanged(inputModel, true))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { cardTransactionService.makeTransaction(any(), any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is check card, then should invoke service.checkCard() method")
    @Test
    fun makeCheckCardRequestOnSubmitFormWithCheckCardWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.CHECK_CARD

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        sut.send(CardEntryAction.ValidationStatusChanged(inputModel, true))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { cardTransactionService.makeTransaction(any(), any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is save card, then should invoke service.saveCard() method")
    @Test
    fun makeSaveCardRequestOnSubmitFormWithSaveCardWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.CREATE_CARD_TOKEN

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        sut.send(CardEntryAction.ValidationStatusChanged(inputModel, true))
        sut.send(CardEntryAction.SubmitForm)

        coVerify { cardTransactionService.makeTransaction(any(), any()) }
    }

    @DisplayName("Given send is called with SubmitForm action, when payment widget type is GOOGLE_PAY, then should throw IllegalArguementException")
    @Test
    fun throwExceptionOnSubmitFormWithGooglePayWidgetType() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        try {
            sut.send(CardEntryAction.SubmitForm)
        } catch (e: IllegalStateException) {
            assertEquals(IllegalStateException::class.java, e::class.java)
        }
    }

    @DisplayName("Given send is called with SubmitForm action, then update judoPaymentResult model")
    @Test
    fun postJudoPaymentResultOnSubmitForm() {
        val slots = mutableListOf<JudoPaymentResult>()
        every { judo.uiConfiguration.avsEnabled } returns true

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.judoPaymentResult.observeForever(judoPaymentResultMock)

        sut.send(CardEntryAction.ValidationStatusChanged(inputModel, true))
        sut.send(CardEntryAction.SubmitForm)

        cardTransactionCallback.captured.onFinish(expectedJudoPaymentResult)

        verify { judoPaymentResultMock.onChanged(capture(slots)) }

        val actualJudoPaymentResult = slots[0]
        assertEquals(expectedJudoPaymentResult, actualJudoPaymentResult)
    }

    @DisplayName("Given send is called with SubmitForm action, when selectedCardNetwork is not null, then update securityCodeResult model")
    @Test
    fun updateSecurityCodeResultModelOnSubmitFormWithSelectedCardNetworkNotNull() {
        val slots = mutableListOf<String>()
        every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS
        selectedCardNetwork = CardNetwork.VISA

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        sut.securityCodeResult.observeForever(securityCodeResultMock)

        sut.send(CardEntryAction.ValidationStatusChanged(inputModel, true))
        sut.send(CardEntryAction.SubmitForm)

        verify { securityCodeResultMock.onChanged(capture(slots)) }

        val result = slots[0]
        assertEquals("452", result)
    }

    @DisplayName("Given payment widget type is payment methods, then button text should be add card")
    @Test
    fun givenPaymentWidgetIsPaymentMethodsThenButtonTextShouldBeAddCard() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

        val mockFormModel = FormModel(
            InputModel(),
            enabledFields,
            judo.supportedCardNetworks.toList(),
            ButtonState.Disabled(R.string.save_card)
        )
        val slots = mutableListOf<CardEntryFragmentModel>()

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
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

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.model.observeForever(modelMock)

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[0]

        assertEquals(CardEntryFragmentModel(mockFormModel), formModel)
    }

    @DisplayName("Given send is called with ScanCard action,then should update model with disabled button")
    @Test
    fun postsJudoApiCallResultOnSubmitForm() {
        mockkStatic("com.judopay.judokit.android.model.CardScanningResultKt")
        val cardScanningResult: CardScanningResult = mockk(relaxed = true)
        every { cardScanningResult.toInputModel() } returns inputModel

        val mockFormModel = FormModel(
            inputModel,
            enabledFields,
            judo.supportedCardNetworks.toList(),
            ButtonState.Disabled(R.string.pay_now)
        )
        val slots = mutableListOf<CardEntryFragmentModel>()

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.model.observeForever(modelMock)

        sut.send(CardEntryAction.ScanCard(cardScanningResult))

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[1]
        assertEquals(CardEntryFragmentModel(mockFormModel), formModel)
    }

    @DisplayName("Given payment widget type is CARD_PAYMENT, when shouldPaymentButtonDisplayAmount is true, then amount should return formatted amount")
    @Test
    fun returnFormattedAmountOnShouldPaymentButtonDisplayAmountTrueWithWidgetTypeCardPayment() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.CARD_PAYMENT
        every { judo.uiConfiguration.shouldPaymentButtonDisplayAmount } returns true

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        assertEquals(judo.amount.formatted, sut.amount)
    }

    @DisplayName("Given payment widget type is PRE_AUTH, when shouldPaymentButtonDisplayAmount is true, then amount should return formatted amount")
    @Test
    fun returnFormattedAmountOnShouldPaymentButtonDisplayAmountTrueWithWidgetTypePreAuth() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH
        every { judo.uiConfiguration.shouldPaymentButtonDisplayAmount } returns true

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        assertEquals(judo.amount.formatted, sut.amount)
    }

    @DisplayName("Given payment widget type is PRE_AUTH, when shouldPaymentButtonDisplayAmount is false, then amount should return null")
    @Test
    fun returnAmountNullWhenShouldPaymentButtonDisplayAmountFalseWithWidgetTypePreAuth() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH
        every { judo.uiConfiguration.shouldPaymentButtonDisplayAmount } returns false

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        assertEquals(null, sut.amount)
    }

    @DisplayName("Given payment widget type is PAYMENT_METHODS, then amount should return null")
    @Test
    fun returnAmountNullWhenWidgetTypePaymentMethods() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH
        every { judo.uiConfiguration.shouldPaymentButtonDisplayAmount } returns false

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        assertEquals(null, sut.amount)
    }

    @DisplayName("Given payment widget type is CARD_PAYMENT, when shouldPaymentButtonDisplayAmount is false then submitButtonText should return pay now")
    @Test
    fun returnPayNowWhenWidgetTypeCardPaymentWithShouldPaymentButtonDisplayAmountFalse() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.CARD_PAYMENT
        every { judo.uiConfiguration.shouldPaymentButtonDisplayAmount } returns false

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        assertEquals(R.string.pay_now, sut.submitButtonText)
    }

    @DisplayName("Given payment widget type is PRE_AUTH, when shouldPaymentButtonDisplayAmount is false then submitButtonText should return pay now")
    @Test
    fun returnPayNowWhenWidgetTypePreAuthWithShouldPaymentButtonDisplayAmountFalse() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH
        every { judo.uiConfiguration.shouldPaymentButtonDisplayAmount } returns false

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        assertEquals(R.string.pay_now, sut.submitButtonText)
    }

    @DisplayName("Given payment widget type is PRE_AUTH, when shouldPaymentButtonDisplayAmount is true then submitButtonText should return pay amount")
    @Test
    fun returnPayAmountWhenWidgetTypePreAuthWithShouldPaymentButtonDisplayAmountTrue() {
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH
        every { judo.uiConfiguration.shouldPaymentButtonDisplayAmount } returns true

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)

        assertEquals(R.string.pay_amount, sut.submitButtonText)
    }

    @DisplayName("Given send is called with EnableFormFields action, then should update model with specified enabled fields")
    @Test
    fun shouldUpdateModelWithSpecifiedEnabledFieldsOnEnableFormFieldsAction() {
        val mockFormModel = FormModel(
            InputModel(),
            listOf(FormFieldType.NUMBER, FormFieldType.SECURITY_NUMBER),
            judo.supportedCardNetworks.toList(),
            ButtonState.Disabled(R.string.pay_now)
        )
        val slots = mutableListOf<CardEntryFragmentModel>()

        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.model.observeForever(modelMock)

        sut.send(CardEntryAction.EnableFormFields(listOf(FormFieldType.NUMBER, FormFieldType.SECURITY_NUMBER)))

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[1]
        assertEquals(CardEntryFragmentModel(mockFormModel), formModel)
    }

    @DisplayName("Given selectedCardNetwork is not null, then should update model with displayScanButton false")
    @Test
    fun shouldUpdateModelWithDisplayScanButtonGoneOnSelectedCardNetworkNotNull() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        selectedCardNetwork = CardNetwork.VISA
        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.model.observeForever(modelMock)

        sut.send(CardEntryAction.EnableFormFields(listOf(FormFieldType.NUMBER, FormFieldType.SECURITY_NUMBER)))

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[1]
        assertFalse(formModel.displayScanButton)
    }

    @DisplayName("Given selectedCardNetwork is null, then should update model with displayScanButton true")
    @Test
    fun shouldUpdateModelWithDisplayScanButtonVisibleOnSelectedCardNetworkNull() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        selectedCardNetwork = null
        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.model.observeForever(modelMock)

        sut.send(
            CardEntryAction.EnableFormFields(
                listOf(
                    FormFieldType.NUMBER,
                    FormFieldType.SECURITY_NUMBER
                )
            )
        )

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[1]
        assertTrue(formModel.displayScanButton)
    }

    @DisplayName("Given scan card dependency is not present, then should update model with displayScanButton false")
    @Test
    fun shouldUpdateModelWithDisplayScanButtonVisibleOnSelectedCardNetworkNullAndScanCardPresent() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        every { isDependencyPresent("cards.pay.paycardsrecognizer.sdk.ScanCardIntent") } returns false
        sut = CardEntryViewModel(judo, cardTransactionService, repository, selectedCardNetwork, application)
        sut.model.observeForever(modelMock)

        sut.send(
            CardEntryAction.EnableFormFields(
                listOf(
                    FormFieldType.NUMBER,
                    FormFieldType.SECURITY_NUMBER
                )
            )
        )

        verify { modelMock.onChanged(capture(slots)) }

        val formModel = slots[1]
        assertFalse(formModel.displayScanButton)
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
        every { authorization } returns mockk(relaxed = true)
        every { amount } returns Amount("1", Currency.EUR)
        every { reference } returns Reference("consumer", "payment")
    }
}
