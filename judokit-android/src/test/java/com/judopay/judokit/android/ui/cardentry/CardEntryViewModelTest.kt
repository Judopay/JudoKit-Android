package com.judopay.judokit.android.ui.cardentry

import android.app.Application
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.judopay.judokit.android.InstantExecutorExtension
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.BasicAuthorization
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.CardScanningResult
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.Reference
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.UiConfiguration
import com.judopay.judokit.android.model.formatted
import com.judopay.judokit.android.model.typeId
import com.judopay.judokit.android.service.CardTransactionManager
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.common.ButtonState
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(InstantExecutorExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class CardEntryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val judo = getJudo(PaymentWidgetType.CARD_PAYMENT)
    private val repository: TokenizedCardRepository = mockk(relaxed = true)
    private val cardTransactionManager: CardTransactionManager = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)

//    private val card: CardToken = mockk(relaxed = true)
//    private var selectedCardNetwork: CardNetwork? = null
//    private val cardEntryOptions = CardEntryOptions()
//    private val inputModel: CardDetailsInputModel = getInputModel()
//    private val billingDetailsInputModel: BillingDetailsInputModel = getBillingDetailsInputModel()
//    private val expectedJudoPaymentResult = JudoPaymentResult.Success(mockk(relaxed = true))

//    private val cardTransactionCallback = slot<CardTransactionManagerResultListener>()
//    private val judoPaymentResultMock = spyk<Observer<JudoPaymentResult?>>()
//    private val securityCodeResultMock = spyk<Observer<String>>()

//    private val enabledFields = listOf(
//        CardDetailsFieldType.NUMBER,
//        CardDetailsFieldType.HOLDER_NAME,
//        CardDetailsFieldType.EXPIRATION_DATE,
//        CardDetailsFieldType.SECURITY_NUMBER
//    )

    private lateinit var sut: CardEntryViewModel
    private val modelSpy = spyk<Observer<CardEntryFragmentModel>>()
    private val judoPaymentResultSpy = spyk<Observer<JudoPaymentResult>>()
    private val cardEntryToPaymentMethodResultSpy = spyk<Observer<TransactionDetails.Builder>>()

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sut = CardEntryViewModel(judo, cardTransactionManager, repository, CardEntryOptions(), application)
        sut.model.observeForever(modelSpy)
        sut.judoPaymentResult.observeForever(judoPaymentResultSpy)
        sut.cardEntryToPaymentMethodResult.observeForever(cardEntryToPaymentMethodResultSpy)

//        mockkStatic("retrofit2.KotlinExtensions")
//        mockkStatic("com.judopay.judokit.android.ui.paymentmethods.MappersKt")
//        mockkStatic("com.judopay.judokit.android.ui.common.FunctionsKt")
//        coEvery { repository.insert(card.toTokenizedCardEntity(application, any())) } returns mockk(relaxed = true)
//        coEvery {
//            cardTransactionService.makeTransaction(
//                any(),
//                capture(cardTransactionCallback)
//            )
//        } coAnswers { cardTransactionCallback }
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        sut.model.removeObserver(modelSpy)
        sut.judoPaymentResult.removeObserver(judoPaymentResultSpy)
        sut.cardEntryToPaymentMethodResult.removeObserver(cardEntryToPaymentMethodResultSpy)
    }

    @Test
    fun `Given CardEntryViewModel initialises, when isLoading = false and isFormValid = false, then buttonState is Disabled`() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Disabled(R.string.pay_now))
    }

    @Test
    fun `Given send is called with InsertCard action, then should call repository updateAllLastUsedToFalse and insert`() =
        runTest {
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(cardHolderName = "Bob"), true))
            val tokenizedCard = CardToken(lastFour = "1234", token = "token", type = CardNetwork.VISA.typeId)
            sut.send(CardEntryAction.InsertCard(tokenizedCard))
            advanceUntilIdle()

            coVerify { repository.updateAllLastUsedToFalse() }
            val slots = mutableListOf<TokenizedCardEntity>()
            coVerify { repository.insert(capture(slots)) }
            assertThat(slots.first().ending).isEqualTo("1234")
            assertThat(slots.first().token).isEqualTo("token")
            assertThat(slots.first().network).isEqualTo(CardNetwork.VISA)
            assertThat(slots.first().cardholderName).isEqualTo("Bob")
        }

    @Test
    fun `Given send is called with ValidationStatusChanged action with isFormValid = true, then buttonState is Enabled`() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))

        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Enabled(R.string.pay_now))
    }

    @Test
    fun `Given send is called with SubmitCardEntryForm action with isLoading = true and isFormValid = true, then buttonState is Loading`() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        sut.send(CardEntryAction.SubmitCardEntryForm)

        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Loading)
    }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CARD_PAYMENT, then cardTransactionManager's payment method is called`() =
        runTest {
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.payment(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is PRE_AUTH, then cardTransactionManager's preAuth method is called`() =
        runTest {
            sut = CardEntryViewModel(
                getJudo(PaymentWidgetType.PRE_AUTH),
                cardTransactionManager,
                repository,
                CardEntryOptions(),
                application
            )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.preAuth(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is REGISTER_CARD, then cardTransactionManager's register method is called`() =
        runTest {
            sut = CardEntryViewModel(
                getJudo(PaymentWidgetType.REGISTER_CARD),
                cardTransactionManager,
                repository,
                CardEntryOptions(),
                application
            )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.register(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CHECK_CARD, then cardTransactionManager's check method is called`() =
        runTest {
            sut = CardEntryViewModel(
                getJudo(PaymentWidgetType.CHECK_CARD),
                cardTransactionManager,
                repository,
                CardEntryOptions(),
                application
            )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.check(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CREATE_CARD_TOKEN, then cardTransactionManager's save method is called`() =
        runTest {
            sut = CardEntryViewModel(
                getJudo(PaymentWidgetType.CREATE_CARD_TOKEN),
                cardTransactionManager,
                repository,
                CardEntryOptions(),
                application
            )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.save(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is TOKEN_PAYMENT, then cardTransactionManager's paymentWithToken method is called`() =
        runTest {
            sut = CardEntryViewModel(
                getJudo(PaymentWidgetType.TOKEN_PAYMENT),
                cardTransactionManager,
                repository,
                CardEntryOptions(),
                application
            )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.paymentWithToken(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is TOKEN_PRE_AUTH, then cardTransactionManager's preAuthWithToken method is called`() =
        runTest {
            sut = CardEntryViewModel(
                getJudo(PaymentWidgetType.TOKEN_PRE_AUTH),
                cardTransactionManager,
                repository,
                CardEntryOptions(),
                application
            )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.preAuthWithToken(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is GOOGLE_PAY, then IllegalStateException is thrown`() =
        runTest {
            sut = CardEntryViewModel(
                getJudo(PaymentWidgetType.GOOGLE_PAY),
                cardTransactionManager,
                repository,
                CardEntryOptions(),
                application
            )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            try {
                sut.send(CardEntryAction.SubmitCardEntryForm)
                advanceUntilIdle()
            } catch (e: IllegalStateException) {
                assertThat(e.message).isEqualTo("Unsupported PaymentWidgetType")
            }
        }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, then update judoPaymentResult model`() {
        sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
        sut.send(CardEntryAction.SubmitCardEntryForm)

        val expectedJudoPaymentResult = JudoPaymentResult.Success(JudoResult())
        sut.onCardTransactionResult(expectedJudoPaymentResult)

        val slots = mutableListOf<JudoPaymentResult>()
        verify { judoPaymentResultSpy.onChanged(capture(slots)) }
        assertThat(slots.last()).isEqualTo(expectedJudoPaymentResult)
    }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, when fromPaymentMethods, then update cardEntryToPaymentMethodResult model`() =
        runTest {
            sut.cardEntryToPaymentMethodResult.removeObserver(cardEntryToPaymentMethodResultSpy)
            sut = CardEntryViewModel(
                getJudo(PaymentWidgetType.PAYMENT_METHODS),
                cardTransactionManager,
                repository,
                CardEntryOptions(fromPaymentMethods = true, shouldDisplaySecurityCode = CardNetwork.VISA),
                application
            )
            sut.cardEntryToPaymentMethodResult.observeForever(cardEntryToPaymentMethodResultSpy)
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(securityNumber = "333"), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            val slots = mutableListOf<TransactionDetails.Builder>()
            verify { cardEntryToPaymentMethodResultSpy.onChanged(capture(slots)) }
            assertThat(slots.last().build().securityNumber).isEqualTo("333")
        }

    @Test
    fun `Given payment widget type is PAYMENT_METHODS, then button text should be 'Save Card'`() {
        sut.model.removeObserver(modelSpy)
        sut = CardEntryViewModel(
            getJudo(PaymentWidgetType.PAYMENT_METHODS),
            cardTransactionManager,
            repository,
            CardEntryOptions(fromPaymentMethods = true, addCardPressed = true),
            application
        )
        sut.model.observeForever(modelSpy)

        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Disabled(R.string.save_card))
    }

    @Test
    fun `Given payment widget type is not supported, then button text should be empty string`() {
        sut.model.removeObserver(modelSpy)
        sut = CardEntryViewModel(
            getJudo(PaymentWidgetType.GOOGLE_PAY),
            cardTransactionManager,
            repository,
            CardEntryOptions(fromPaymentMethods = true, addCardPressed = true),
            application
        )
        sut.model.observeForever(modelSpy)

        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Disabled(R.string.empty))
    }

    @Test
    fun `Given send is called with ScanCard action, then set model properties accordingly and buttonState is Disabled`() {
        val cardScanningResult = CardScanningResult(cardNumber = "1234123412341234", cardHolder = "Bob", expirationDate = "12/25")
        sut.send(CardEntryAction.ScanCard(cardScanningResult))

        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.cardNumber).isEqualTo(cardScanningResult.cardNumber)
        assertThat(inputModel.cardHolderName).isEqualTo(cardScanningResult.cardHolder)
        assertThat(inputModel.expirationDate).isEqualTo(cardScanningResult.expirationDate)
    }

    @Test
    fun `Given payment widget type is CARD_PAYMENT, when shouldPaymentButtonDisplayAmount is true, then button should say formatted amount`() {
        sut.model.removeObserver(modelSpy)
        val uiConfiguration = UiConfiguration.Builder().setShouldPaymentButtonDisplayAmount(true).build()
        val judo = getJudo(PaymentWidgetType.CARD_PAYMENT, uiConfiguration)
        sut = CardEntryViewModel(judo, cardTransactionManager, repository, CardEntryOptions(), application)
        sut.model.observeForever(modelSpy)

        assertThat(sut.amount).isEqualTo(judo.amount.formatted)
        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Disabled(R.string.pay_amount, judo.amount.formatted))
    }

    @Test
    fun `Given payment widget type is CARD_PAYMENT, when shouldPaymentButtonDisplayAmount is false, then button should not say formatted amount`() {
        sut.model.removeObserver(modelSpy)
        val uiConfiguration = UiConfiguration.Builder().setShouldPaymentButtonDisplayAmount(false).build()
        val judo = getJudo(PaymentWidgetType.CARD_PAYMENT, uiConfiguration)
        sut = CardEntryViewModel(judo, cardTransactionManager, repository, CardEntryOptions(), application)
        sut.model.observeForever(modelSpy)

        assertThat(sut.amount).isNull()
        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Disabled(R.string.pay_now))
    }

    @Test
    fun `Given payment widget type is PRE_AUTH, when shouldPaymentButtonDisplayAmount is true, then button should say formatted amount`() {
        sut.model.removeObserver(modelSpy)
        val uiConfiguration = UiConfiguration.Builder().setShouldPaymentButtonDisplayAmount(true).build()
        val judo = getJudo(PaymentWidgetType.PRE_AUTH, uiConfiguration)
        sut = CardEntryViewModel(judo, cardTransactionManager, repository, CardEntryOptions(), application)
        sut.model.observeForever(modelSpy)

        assertThat(sut.amount).isEqualTo(judo.amount.formatted)
        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Disabled(R.string.pay_amount, judo.amount.formatted))
    }

    @Test
    fun `Given payment widget type is PRE_AUTH, when shouldPaymentButtonDisplayAmount is false, then button should not say formatted amount`() {
        sut.model.removeObserver(modelSpy)
        val uiConfiguration = UiConfiguration.Builder().setShouldPaymentButtonDisplayAmount(false).build()
        val judo = getJudo(PaymentWidgetType.PRE_AUTH, uiConfiguration)
        sut = CardEntryViewModel(judo, cardTransactionManager, repository, CardEntryOptions(), application)
        sut.model.observeForever(modelSpy)

        assertThat(sut.amount).isNull()
        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Disabled(R.string.pay_now))
    }

    @Test
    fun `Given payment widget type is PAYMENT_METHODS, when shouldPaymentButtonDisplayAmount is false, then button should not say formatted amount`() {
        sut.model.removeObserver(modelSpy)
        val judo = getJudo(PaymentWidgetType.PAYMENT_METHODS)
        sut = CardEntryViewModel(judo, cardTransactionManager, repository, CardEntryOptions(), application)
        sut.model.observeForever(modelSpy)

        assertThat(sut.amount).isNull()
        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.buttonState).isEqualTo(ButtonState.Disabled(R.string.pay_now))
    }

    @Test
    fun `Given send is called with EnableFormFields action, then should update model with specified enabled fields`() {
        val enabledFields = listOf(CardDetailsFieldType.NUMBER, CardDetailsFieldType.SECURITY_NUMBER)
        sut.send(CardEntryAction.EnableFormFields(enabledFields))

        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.enabledFields).isEqualTo(enabledFields)
    }

    @Test
    fun `Given CardEntryViewModel initialises, shouldDisplayScanButton is always false`() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        assertThat(slots.last().displayScanButton).isFalse()
    }

    @Test
    fun `Given CardEntryViewModel initialises, shouldDisplayBackButton is true if fromPaymentMethods and shouldDisplaySecurityCode is not null`() {
        sut.model.removeObserver(modelSpy)
        sut = CardEntryViewModel(
            getJudo(PaymentWidgetType.GOOGLE_PAY),
            cardTransactionManager,
            repository,
            CardEntryOptions(fromPaymentMethods = true, shouldDisplaySecurityCode = CardNetwork.VISA),
            application
        )
        sut.model.observeForever(modelSpy)

        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        assertThat(slots.last().displayBackButton).isTrue()
    }

    private fun getJudo(widgetType: PaymentWidgetType, uiConfiguration: UiConfiguration = UiConfiguration.Builder().build()) =
        Judo.Builder(widgetType)
            .setJudoId("123456789")
            .setAuthorization(mockk<BasicAuthorization>(relaxed = true))
            .setAmount(Amount(amount = "1", currency = Currency.GBP))
            .setReference(Reference(consumerReference = "ref", paymentReference = UUID.randomUUID().toString()))
            .setUiConfiguration(uiConfiguration)
            .build()
}
