package com.judopay.judokit.android.ui.cardentry

import android.app.Application
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.google.common.truth.Truth.assertThat
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
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.common.ButtonState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
internal class CardEntryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val judo = getJudo(PaymentWidgetType.CARD_PAYMENT)
    private val repository: TokenizedCardRepository = mockk(relaxed = true)
    private val cardTransactionRepository: CardTransactionRepository = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)

    private lateinit var sut: CardEntryViewModel

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        Locale.setDefault(Locale.UK)

        mockkStatic("androidx.core.os.ConfigurationCompat")

        every {
            ConfigurationCompat.getLocales(any())
        } returns LocaleListCompat.getEmptyLocaleList()

        val inputStream: InputStream =
            ByteArrayInputStream(
                (
                    "[{ \"alpha2Code\": \"GB\"," +
                        " \"name\": \"United Kingdom\"," +
                        " \"dialCode\": \"44\"," +
                        " \"numericCode\": \"826\"," +
                        " \"phoneNumberFormat\": \"#### ######\" }]"
                ).toByteArray(
                    StandardCharsets.UTF_8,
                ),
            )

        every { application.assets.open("countries.json") } returns inputStream

        coEvery { cardTransactionRepository.payment(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.preAuth(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.check(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.save(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.paymentWithToken(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.preAuthWithToken(any(), any()) } returns JudoPaymentResult.Success(JudoResult())

        sut = CardEntryViewModel(judo, cardTransactionRepository, repository, CardEntryOptions(), application)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given CardEntryViewModel initialises, when isLoading = false and isFormValid = false, then buttonState is Disabled`() {
        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_pay_now))
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
        sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Enabled(R.string.jp_pay_now))
    }

    @Test
    fun `Given send is called with SubmitCardEntryForm action with isLoading = true and isFormValid = true, then buttonState is Loading`() {
        sut.send(CardEntryAction.SubmitCardEntryForm)

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Loading)
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CARD_PAYMENT, then cardTransactionRepository's payment method is called`() =
        runTest {
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.payment(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is PRE_AUTH, then cardTransactionRepository's preAuth method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.PRE_AUTH),
                    cardTransactionRepository,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.preAuth(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CHECK_CARD, then cardTransactionRepository's check method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.CHECK_CARD),
                    cardTransactionRepository,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.check(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CREATE_CARD_TOKEN, then cardTransactionRepository's save method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.CREATE_CARD_TOKEN),
                    cardTransactionRepository,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.save(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is TOKEN_PAYMENT, then cardTransactionRepository's paymentWithToken method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.TOKEN_PAYMENT),
                    cardTransactionRepository,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.paymentWithToken(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is TOKEN_PRE_AUTH, then cardTransactionRepository's preAuthWithToken method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.TOKEN_PRE_AUTH),
                    cardTransactionRepository,
                    repository,
                    CardEntryOptions(),
                    application,
                )
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.preAuthWithToken(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is GOOGLE_PAY, then IllegalStateException is thrown`() {
        val exception =
            assertThrows<IllegalStateException> {
                runTest {
                    sut =
                        CardEntryViewModel(
                            getJudo(PaymentWidgetType.GOOGLE_PAY),
                            cardTransactionRepository,
                            repository,
                            CardEntryOptions(),
                            application,
                        )
                    sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
                    sut.send(CardEntryAction.SubmitCardEntryForm)
                    advanceUntilIdle()
                }
            }
        assertThat(exception.message).isEqualTo("Unsupported PaymentWidgetType")
    }

    @Test
    fun `Given send is called with SubmitCardEntryForm action, then update judoPaymentResult model`() =
        runTest {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(results::add)
            }

            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            assertThat(results).isNotEmpty()
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when fromPaymentMethods, then update cardEntryToPaymentMethodResult model`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.PAYMENT_METHODS),
                    cardTransactionRepository,
                    repository,
                    CardEntryOptions(isPresentedFromPaymentMethods = true, cardNetwork = CardNetwork.VISA),
                    application,
                )
            val results = mutableListOf<TransactionDetails.Builder>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.cardEntryToPaymentMethodResultEffect.collect(results::add)
            }

            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(securityNumber = "333"), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            assertThat(results.last().build().securityNumber).isEqualTo("333")
        }

    @Test
    fun `Given payment widget type is PAYMENT_METHODS, then button text should be 'Save Card'`() {
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.PAYMENT_METHODS),
                cardTransactionRepository,
                repository,
                CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = true),
                application,
            )

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_save_card))
    }

    @Test
    fun `Given payment widget type is GOOGLE_PAY, then button text should be empty string`() {
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.GOOGLE_PAY),
                cardTransactionRepository,
                repository,
                CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = true),
                application,
            )

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_empty))
    }

    @Test
    fun `Given payment widget type is PRE_AUTH_GOOGLE_PAY, then button text should be empty string`() {
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.PRE_AUTH_GOOGLE_PAY),
                cardTransactionRepository,
                repository,
                CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = true),
                application,
            )

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_empty))
    }

    @Test
    fun `Given send is called with ScanCard action, then set model properties accordingly and buttonState is Disabled`() {
        val cardScanningResult = CardScanningResult(cardNumber = "1234123412341234", cardHolder = "Bob", expirationDate = "12/25")
        sut.send(CardEntryAction.ScanCard(cardScanningResult))

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.cardNumber).isEqualTo(cardScanningResult.cardNumber)
        assertThat(inputModel?.cardHolderName).isEqualTo(cardScanningResult.cardHolder)
        assertThat(inputModel?.expirationDate).isEqualTo(cardScanningResult.expirationDate)
    }

    @Test
    fun `Given fromPaymentMethods and shouldAskForCSC and shouldAskForCardholderName, then initialise enableFormFields correctly`() {
        val judo =
            getJudo(
                PaymentWidgetType.CARD_PAYMENT,
                UiConfiguration
                    .Builder()
                    .setShouldAskForCSC(true)
                    .setShouldAskForCardholderName(true)
                    .build(),
            )
        sut =
            CardEntryViewModel(
                judo,
                cardTransactionRepository,
                repository,
                CardEntryOptions(isPresentedFromPaymentMethods = true),
                application,
            )

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.enabledFields).isEqualTo(
            listOf(
                CardDetailsFieldType.SECURITY_NUMBER,
                CardDetailsFieldType.HOLDER_NAME,
            ),
        )
    }

    @Test
    fun `Given send is called with PressBackButton action, then should navigate back to Card`() =
        runTest {
            val results = mutableListOf<CardEntryNavigation>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.navigationEffect.collect(results::add)
            }

            sut.send(CardEntryAction.PressBackButton)
            advanceUntilIdle()

            assertThat(results.last()).isEqualTo(CardEntryNavigation.Card)
        }

    private fun getJudo(
        widgetType: PaymentWidgetType,
        uiConfiguration: UiConfiguration = UiConfiguration.Builder().build(),
    ) = Judo
        .Builder(widgetType)
        .setJudoId("123456789")
        .setAuthorization(mockk<PaymentSessionAuthorization>(relaxed = true))
        .setAmount(Amount(amount = "1", currency = Currency.GBP))
        .setReference(Reference(consumerReference = "ref", paymentReference = UUID.randomUUID().toString()))
        .setUiConfiguration(uiConfiguration)
        .build()
}
