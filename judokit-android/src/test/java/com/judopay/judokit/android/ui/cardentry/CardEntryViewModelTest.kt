package com.judopay.judokit.android.ui.cardentry

import android.app.Application
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.judopay.judokit.android.InstantExecutorExtension
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.PaymentSessionAuthorization
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
import org.junit.jupiter.api.Disabled
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

    private lateinit var sut: CardEntryViewModel
    private val modelSpy = spyk<Observer<CardEntryFragmentModel>>()
    private val judoPaymentResultSpy = spyk<Observer<JudoPaymentResult>>()
    private val cardEntryToPaymentMethodResultSpy = spyk<Observer<TransactionDetails.Builder>>()
    private val navigationObserverSpy = spyk<Observer<CardEntryNavigation>>()

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sut = CardEntryViewModel(judo, cardTransactionManager, repository, CardEntryOptions(), application)
        sut.model.observeForever(modelSpy)
        sut.judoPaymentResult.observeForever(judoPaymentResultSpy)
        sut.cardEntryToPaymentMethodResult.observeForever(cardEntryToPaymentMethodResultSpy)
        sut.navigationObserver.observeForever(navigationObserverSpy)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        sut.model.removeObserver(modelSpy)
        sut.judoPaymentResult.removeObserver(judoPaymentResultSpy)
        sut.cardEntryToPaymentMethodResult.removeObserver(cardEntryToPaymentMethodResultSpy)
        sut.navigationObserver.removeObserver(navigationObserverSpy)
    }

    @Test
    fun `Given CardEntryViewModel initialises, when isLoading = false and isFormValid = false, then buttonState is Disabled`() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.pay_now))
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
        assertThat(inputModel.actionButtonState).isEqualTo(ButtonState.Enabled(R.string.pay_now))
    }

    @Test
    fun `Given send is called with SubmitCardEntryForm action with isLoading = true and isFormValid = true, then buttonState is Loading`() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        sut.send(CardEntryAction.SubmitCardEntryForm)

        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.actionButtonState).isEqualTo(ButtonState.Loading)
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CARD_PAYMENT, then cardTransactionManager's payment method is called`() =
        runTest {
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.payment(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is PRE_AUTH, then cardTransactionManager's preAuth method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.PRE_AUTH),
                    cardTransactionManager,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.preAuth(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is REGISTER_CARD, then cardTransactionManager's register method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.REGISTER_CARD),
                    cardTransactionManager,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.register(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CHECK_CARD, then cardTransactionManager's check method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.CHECK_CARD),
                    cardTransactionManager,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.check(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CREATE_CARD_TOKEN, then cardTransactionManager's save method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.CREATE_CARD_TOKEN),
                    cardTransactionManager,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.save(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is TOKEN_PAYMENT, then cardTransactionManager's paymentWithToken method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.TOKEN_PAYMENT),
                    cardTransactionManager,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.paymentWithToken(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is TOKEN_PRE_AUTH, then cardTransactionManager's preAuthWithToken method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.TOKEN_PRE_AUTH),
                    cardTransactionManager,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionManager.preAuthWithToken(any(), CardEntryViewModel::class.java.name) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @Disabled
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is GOOGLE_PAY, then IllegalStateException is thrown`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.GOOGLE_PAY),
                    cardTransactionManager,
                    repository,
                    CardEntryOptions(),
                    application,
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
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when fromPaymentMethods, then update cardEntryToPaymentMethodResult model`() =
        runTest {
            sut.cardEntryToPaymentMethodResult.removeObserver(cardEntryToPaymentMethodResultSpy)
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.PAYMENT_METHODS),
                    cardTransactionManager,
                    repository,
                    CardEntryOptions(isPresentedFromPaymentMethods = true, cardNetwork = CardNetwork.VISA),
                    application,
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
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.PAYMENT_METHODS),
                cardTransactionManager,
                repository,
                CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = true),
                application,
            )
        sut.model.observeForever(modelSpy)

        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.save_card))
    }

    @Test
    fun `Given payment widget type is GOOGLE_PAY, then button text should be empty string`() {
        sut.model.removeObserver(modelSpy)
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.GOOGLE_PAY),
                cardTransactionManager,
                repository,
                CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = true),
                application,
            )
        sut.model.observeForever(modelSpy)

        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.empty))
    }

    @Test
    fun `Given payment widget type is PRE_AUTH_GOOGLE_PAY, then button text should be empty string`() {
        sut.model.removeObserver(modelSpy)
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.PRE_AUTH_GOOGLE_PAY),
                cardTransactionManager,
                repository,
                CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = true),
                application,
            )
        sut.model.observeForever(modelSpy)

        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.empty))
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
    fun `Given fromPaymentMethods and shouldAskForCSC and shouldAskForCardholderName, then initialise enableFormFields correctly`() {
        sut.model.removeObserver(modelSpy)
        val judo =
            getJudo(
                PaymentWidgetType.CARD_PAYMENT,
                UiConfiguration.Builder().setShouldAskForCSC(true).setShouldAskForCardholderName(true).build(),
            )
        sut =
            CardEntryViewModel(
                judo,
                cardTransactionManager,
                repository,
                CardEntryOptions(isPresentedFromPaymentMethods = true),
                application,
            )
        sut.model.observeForever(modelSpy)

        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        val inputModel = slots.last().formModel.cardDetailsInputModel
        assertThat(inputModel.enabledFields).isEqualTo(
            listOf(
                CardDetailsFieldType.SECURITY_NUMBER,
                CardDetailsFieldType.HOLDER_NAME,
            ),
        )
    }

    @Test
    fun `Given send is called with PressBackButton action, then should navigate back to Card`() {
        sut.send(CardEntryAction.PressBackButton)

        val slots = mutableListOf<CardEntryNavigation>()
        verify { navigationObserverSpy.onChanged(capture(slots)) }
        assertThat(slots.last()).isEqualTo(CardEntryNavigation.Card)
    }

    @Test
    fun `Given CardEntryViewModel initialises, shouldDisplayScanButton is always false`() {
        val slots = mutableListOf<CardEntryFragmentModel>()
        verify { modelSpy.onChanged(capture(slots)) }
        assertThat(slots.last().displayScanButton).isFalse()
    }

    private fun getJudo(
        widgetType: PaymentWidgetType,
        uiConfiguration: UiConfiguration = UiConfiguration.Builder().build(),
    ) = Judo.Builder(widgetType)
        .setJudoId("123456789")
        .setAuthorization(mockk<PaymentSessionAuthorization>(relaxed = true))
        .setAmount(Amount(amount = "1", currency = Currency.GBP))
        .setReference(Reference(consumerReference = "ref", paymentReference = UUID.randomUUID().toString()))
        .setUiConfiguration(uiConfiguration)
        .build()
}
