package com.judopay.judokit.android.ui.cardentry

import android.app.Application
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.google.common.truth.Truth.assertThat
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.PaymentSessionAuthorization
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.collectFlow
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.CardScanningResult
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.Reference
import com.judopay.judokit.android.model.UiConfiguration
import com.judopay.judokit.android.model.typeId
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsInputModel
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
import kotlinx.coroutines.test.StandardTestDispatcher
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

@Suppress("LargeClass", "DEPRECATION")
@OptIn(ExperimentalCoroutinesApi::class)
internal class CardEntryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val judo = getJudo(PaymentWidgetType.CARD_PAYMENT)
    private val repository: TokenizedCardRepository = mockk(relaxed = true)
    private val cardTransactionRepository: CardTransactionRepository = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)

    private lateinit var sut: CardEntryViewModel
    private val defaultLocale: Locale = Locale.getDefault()

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
        Locale.setDefault(defaultLocale)
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
            val results = collectFlow(sut.paymentResultEffect)

            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            assertThat(results).isNotEmpty()
            assertThat(results.last()).isInstanceOf(JudoPaymentResult.Success::class.java)
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
            val results = collectFlow(sut.cardEntryToPaymentMethodResultEffect)

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
            val results = collectFlow(sut.navigationEffect)

            sut.send(CardEntryAction.PressBackButton)
            advanceUntilIdle()

            assertThat(results.last()).isEqualTo(CardEntryNavigation.Card)
        }

    @Test
    fun `Given send is called with ValidationStatusChanged isFormValid=false after true, then buttonState reverts to Disabled`() {
        sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
        sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), false))

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_pay_now))
    }

    @Test
    fun `Given shouldAskForBillingInformation=true and SubmitCardEntryForm, then Billing navigation is emitted`() =
        runTest {
            val judo =
                getJudo(
                    PaymentWidgetType.CARD_PAYMENT,
                    UiConfiguration.Builder().setShouldAskForBillingInformation(true).build(),
                )
            sut = CardEntryViewModel(judo, cardTransactionRepository, repository, CardEntryOptions(), application)

            val navResults = collectFlow(sut.navigationEffect)

            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            assertThat(navResults.last()).isEqualTo(CardEntryNavigation.Billing)
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given shouldAskForBillingInformation=true and SubmitCardEntryForm, then payment repository is NOT called immediately`() =
        runTest {
            val judo =
                getJudo(
                    PaymentWidgetType.CARD_PAYMENT,
                    UiConfiguration.Builder().setShouldAskForBillingInformation(true).build(),
                )
            sut = CardEntryViewModel(judo, cardTransactionRepository, repository, CardEntryOptions(), application)

            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify(exactly = 0) { cardTransactionRepository.payment(any(), any()) }
        }

    @Test
    fun `Given shouldAskForBillingInformation=true and continueButtonText, then button shows jp_continue_text`() {
        val judo =
            getJudo(
                PaymentWidgetType.CARD_PAYMENT,
                UiConfiguration.Builder().setShouldAskForBillingInformation(true).build(),
            )
        sut = CardEntryViewModel(judo, cardTransactionRepository, repository, CardEntryOptions(), application)

        sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Enabled(R.string.jp_continue_text))
    }

    @Test
    fun `Given send is called with SubmitBillingDetailsForm, then cardTransactionRepository payment is called`() =
        runTest {
            sut.send(CardEntryAction.SubmitBillingDetailsForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.payment(any(), any()) }
        }

    @Test
    fun `Given send is called with SubmitBillingDetailsForm, then paymentResultEffect is emitted`() =
        runTest {
            val results = collectFlow(sut.paymentResultEffect)

            sut.send(CardEntryAction.SubmitBillingDetailsForm)
            advanceUntilIdle()

            assertThat(results).isNotEmpty()
        }

    @Test
    fun `Given send is called with SubmitBillingDetailsForm, then billing button state is Loading`() {
        sut.send(CardEntryAction.SubmitBillingDetailsForm)

        val billingModel =
            sut.uiState.value
                ?.formModel
                ?.billingDetailsInputModel
        assertThat(billingModel?.submitButtonState).isEqualTo(ButtonState.Loading)
    }

    @Test
    fun `Given send is called with BillingDetailsValidationStatusChanged isFormValid=true, then billing submit button is Enabled`() {
        sut.send(CardEntryAction.BillingDetailsValidationStatusChanged(BillingDetailsInputModel(), true))

        val billingModel =
            sut.uiState.value
                ?.formModel
                ?.billingDetailsInputModel
        assertThat(billingModel?.submitButtonState).isEqualTo(ButtonState.Enabled(R.string.jp_pay_now))
    }

    @Test
    fun `Given send is called with BillingDetailsValidationStatusChanged isFormValid=false, then billing submit button is Disabled`() {
        sut.send(CardEntryAction.BillingDetailsValidationStatusChanged(BillingDetailsInputModel(), false))

        val billingModel =
            sut.uiState.value
                ?.formModel
                ?.billingDetailsInputModel
        assertThat(billingModel?.submitButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_pay_now))
    }

    @Test
    fun `Given Initialize for TOKEN_PAYMENT with shouldAskForBillingInformation=true, then Billing navigation is emitted`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(
                        PaymentWidgetType.TOKEN_PAYMENT,
                        UiConfiguration.Builder().setShouldAskForBillingInformation(true).build(),
                    ),
                    cardTransactionRepository,
                    repository,
                    CardEntryOptions(),
                    application,
                )

            val navResults = collectFlow(sut.navigationEffect)

            sut.send(CardEntryAction.Initialize)
            advanceUntilIdle()

            assertThat(navResults.last()).isEqualTo(CardEntryNavigation.Billing)
        }

    @Test
    fun `Given Initialize for TOKEN_PAYMENT with no user input required, then paymentWithToken is called immediately`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.TOKEN_PAYMENT),
                    cardTransactionRepository,
                    repository,
                    CardEntryOptions(),
                    application,
                )

            sut.send(CardEntryAction.Initialize)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.paymentWithToken(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is PRE_AUTH_PAYMENT_METHODS, then cardTransactionRepository's save method is called`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS),
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
    fun `Given avsEnabled=true and no billing information, then COUNTRY and POST_CODE fields are enabled`() {
        sut =
            CardEntryViewModel(
                getJudo(
                    PaymentWidgetType.CARD_PAYMENT,
                    UiConfiguration.Builder().setAvsEnabled(true).build(),
                ),
                cardTransactionRepository,
                repository,
                CardEntryOptions(),
                application,
            )

        val enabledFields =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
                ?.enabledFields
        assertThat(enabledFields).contains(CardDetailsFieldType.COUNTRY)
        assertThat(enabledFields).contains(CardDetailsFieldType.POST_CODE)
    }

    @Test
    fun `Given standard CARD_PAYMENT with no AVS, then standard card fields are enabled without COUNTRY or POST_CODE`() {
        val enabledFields =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
                ?.enabledFields
        assertThat(enabledFields).containsAtLeast(
            CardDetailsFieldType.NUMBER,
            CardDetailsFieldType.HOLDER_NAME,
            CardDetailsFieldType.EXPIRATION_DATE,
            CardDetailsFieldType.SECURITY_NUMBER,
        )
        assertThat(enabledFields).doesNotContain(CardDetailsFieldType.COUNTRY)
        assertThat(enabledFields).doesNotContain(CardDetailsFieldType.POST_CODE)
    }

    @Test
    fun `Given payment returns an error, then paymentResultEffect emits the error result`() =
        runTest {
            val error = JudoPaymentResult.Error(JudoError(message = "network error"))
            coEvery { cardTransactionRepository.payment(any(), any()) } returns error

            val results = collectFlow(sut.paymentResultEffect)

            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            assertThat(results.last()).isEqualTo(error)
        }

    @Test
    fun `Given TOKEN_PAYMENT with no user input required, then uiState isUserInputRequired is false`() {
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.TOKEN_PAYMENT),
                cardTransactionRepository,
                repository,
                CardEntryOptions(),
                application,
            )

        assertThat(sut.uiState.value?.isUserInputRequired).isFalse()
    }

    @Test
    fun `Given CARD_PAYMENT, then uiState isUserInputRequired is true`() {
        assertThat(sut.uiState.value?.isUserInputRequired).isTrue()
    }

    @Test
    fun `Given TOKEN_PAYMENT with no enabled card fields, then billing back button is Hidden`() {
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.TOKEN_PAYMENT),
                cardTransactionRepository,
                repository,
                CardEntryOptions(),
                application,
            )

        val billingModel =
            sut.uiState.value
                ?.formModel
                ?.billingDetailsInputModel
        assertThat(billingModel?.backButtonState).isEqualTo(ButtonState.Hidden)
    }

    @Test
    fun `Given CARD_PAYMENT with enabled card fields, then billing back button is Enabled`() {
        val billingModel =
            sut.uiState.value
                ?.formModel
                ?.billingDetailsInputModel
        assertThat(billingModel?.backButtonState).isEqualTo(ButtonState.Enabled(R.string.jp_back))
    }

    @Test
    fun `Given payment widget type is CHECK_CARD, then button text should be jp_check_card`() {
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.CHECK_CARD),
                cardTransactionRepository,
                repository,
                CardEntryOptions(),
                application,
            )

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_check_card))
    }

    @Test
    fun `Given payment widget type is PAYMENT_METHODS and isAddingNewCard=false, then button text should be jp_pay_now`() {
        sut =
            CardEntryViewModel(
                getJudo(PaymentWidgetType.PAYMENT_METHODS),
                cardTransactionRepository,
                repository,
                CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = false),
                application,
            )

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_pay_now))
    }

    @Test
    fun `Given send is called with ScanCard action, then actionButtonState is Enabled`() {
        val cardScanningResult = CardScanningResult(cardNumber = "4111111111111111", cardHolder = "Alice", expirationDate = "01/28")
        sut.send(CardEntryAction.ScanCard(cardScanningResult))

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Enabled(R.string.jp_pay_now))
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm from payment methods, then cardEntryToPaymentMethodResultEffect contains billing address fields`() =
        runTest {
            sut =
                CardEntryViewModel(
                    getJudo(PaymentWidgetType.PAYMENT_METHODS),
                    cardTransactionRepository,
                    repository,
                    CardEntryOptions(isPresentedFromPaymentMethods = true),
                    application,
                )

            val results = collectFlow(sut.cardEntryToPaymentMethodResultEffect)

            val billingInput =
                BillingDetailsInputModel(
                    email = "test@example.com",
                    city = "London",
                    postalCode = "W1A 1AA",
                )
            sut.send(CardEntryAction.BillingDetailsValidationStatusChanged(billingInput, true))
            sut.send(CardEntryAction.ValidationStatusChanged(CardDetailsInputModel(), true))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            val built = results.last().build()
            assertThat(built.email).isEqualTo("test@example.com")
            assertThat(built.city).isEqualTo("London")
            assertThat(built.postalCode).isEqualTo("W1A 1AA")
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
