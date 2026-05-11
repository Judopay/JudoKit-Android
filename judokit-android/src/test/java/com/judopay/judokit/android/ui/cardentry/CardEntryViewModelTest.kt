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
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.BillingDetailsFormValidator
import com.judopay.judokit.android.ui.cardentry.validation.CardDetailsFormValidator
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
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

    private val mockCardValidator: CardDetailsFormValidator = mockk(relaxed = true)
    private val mockBillingValidator: BillingDetailsFormValidator = mockk(relaxed = true)

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

        // Configure billing mock: isoCodeForAdminDivision needed by handleBillingFieldChanged
        every { mockBillingValidator.isoCodeForAdminDivision(any()) } returns null
        every { mockBillingValidator.adminDivisionRequired } returns false

        // Default: all validation calls return valid so helpers can make forms valid.
        every { mockCardValidator.validateField(any(), any(), any()) } returns ValidationResult(true, R.string.jp_empty)
        every { mockBillingValidator.validateField(any(), any(), any(), any(), any()) } returns ValidationResult(true, R.string.jp_empty)

        coEvery { cardTransactionRepository.payment(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.preAuth(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.check(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.save(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.paymentWithToken(any(), any()) } returns JudoPaymentResult.Success(JudoResult())
        coEvery { cardTransactionRepository.preAuthWithToken(any(), any()) } returns JudoPaymentResult.Success(JudoResult())

        sut = createViewModel()
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        Locale.setDefault(defaultLocale)
    }

    // Creates the SUT with mock validators injected.
    private fun createViewModel(
        widgetType: PaymentWidgetType = PaymentWidgetType.CARD_PAYMENT,
        uiConfig: UiConfiguration = UiConfiguration.Builder().build(),
        options: CardEntryOptions = CardEntryOptions(),
    ): CardEntryViewModel =
        CardEntryViewModel(
            getJudo(widgetType, uiConfig),
            cardTransactionRepository,
            repository,
            options,
            application,
            cardDetailsFormValidator = mockCardValidator,
            billingDetailsFormValidator = mockBillingValidator,
        )

    // Sends FOCUS_CHANGED for all enabled card fields so the ViewModel marks them valid.
    // With the relaxed mock, validateField returns null → treated as valid.
    private fun makeCardFormValid(holderName: String = "John Doe") {
        val enabled =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
                ?.enabledFields ?: emptyList()
        enabled.forEach { type ->
            val value =
                when (type) {
                    CardDetailsFieldType.HOLDER_NAME -> holderName
                    CardDetailsFieldType.SECURITY_NUMBER -> "333"
                    else -> "test"
                }
            sut.send(CardEntryAction.CardFieldChanged(type, value, FormFieldEvent.FOCUS_CHANGED))
        }
    }

    // Sends FOCUS_CHANGED for billing fields that are not pre-seeded as valid in the ViewModel init.
    private fun makeBillingFormValid(
        email: String = "test@test.com",
        country: String = "United Kingdom",
        city: String = "London",
        postalCode: String = "W1A 1AA",
        addressLine1: String = "123 Test Street",
    ) {
        sut.send(CardEntryAction.BillingFieldChanged(BillingDetailsFieldType.EMAIL, email, FormFieldEvent.FOCUS_CHANGED))
        sut.send(CardEntryAction.BillingFieldChanged(BillingDetailsFieldType.COUNTRY, country, FormFieldEvent.FOCUS_CHANGED))
        sut.send(CardEntryAction.BillingFieldChanged(BillingDetailsFieldType.CITY, city, FormFieldEvent.FOCUS_CHANGED))
        sut.send(CardEntryAction.BillingFieldChanged(BillingDetailsFieldType.POST_CODE, postalCode, FormFieldEvent.FOCUS_CHANGED))
        sut.send(CardEntryAction.BillingFieldChanged(BillingDetailsFieldType.ADDRESS_LINE_1, addressLine1, FormFieldEvent.FOCUS_CHANGED))
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
            sut.send(CardEntryAction.CardFieldChanged(CardDetailsFieldType.HOLDER_NAME, "Bob", FormFieldEvent.FOCUS_CHANGED))
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
    fun `Given all card fields are changed to valid, then buttonState is Enabled`() {
        makeCardFormValid()

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
            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.payment(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is PRE_AUTH, then cardTransactionRepository's preAuth method is called`() =
        runTest {
            sut = createViewModel(PaymentWidgetType.PRE_AUTH)
            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.preAuth(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CHECK_CARD, then cardTransactionRepository's check method is called`() =
        runTest {
            sut = createViewModel(PaymentWidgetType.CHECK_CARD)
            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.check(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is CREATE_CARD_TOKEN, then cardTransactionRepository's save method is called`() =
        runTest {
            sut = createViewModel(PaymentWidgetType.CREATE_CARD_TOKEN)
            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.save(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is TOKEN_PAYMENT, then cardTransactionRepository's paymentWithToken method is called`() =
        runTest {
            sut = createViewModel(PaymentWidgetType.TOKEN_PAYMENT)
            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.paymentWithToken(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is TOKEN_PRE_AUTH, then cardTransactionRepository's preAuthWithToken method is called`() =
        runTest {
            sut = createViewModel(PaymentWidgetType.TOKEN_PRE_AUTH)
            makeCardFormValid()
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
                    sut = createViewModel(PaymentWidgetType.GOOGLE_PAY)
                    makeCardFormValid()
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

            makeCardFormValid()
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
                createViewModel(
                    PaymentWidgetType.PAYMENT_METHODS,
                    options = CardEntryOptions(isPresentedFromPaymentMethods = true, cardNetwork = CardNetwork.VISA),
                )
            val results = collectFlow(sut.cardEntryToPaymentMethodResultEffect)

            sut.send(CardEntryAction.CardFieldChanged(CardDetailsFieldType.SECURITY_NUMBER, "333", FormFieldEvent.FOCUS_CHANGED))
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            assertThat(results.last().build().securityNumber).isEqualTo("333")
        }

    @Test
    fun `Given payment widget type is PAYMENT_METHODS, then button text should be 'Save Card'`() {
        sut =
            createViewModel(
                PaymentWidgetType.PAYMENT_METHODS,
                options = CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = true),
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
            createViewModel(
                PaymentWidgetType.GOOGLE_PAY,
                options = CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = true),
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
            createViewModel(
                PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
                options = CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = true),
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
        sut =
            createViewModel(
                PaymentWidgetType.CARD_PAYMENT,
                UiConfiguration
                    .Builder()
                    .setShouldAskForCSC(true)
                    .setShouldAskForCardholderName(true)
                    .build(),
                CardEntryOptions(isPresentedFromPaymentMethods = true),
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
    fun `Given a card field is invalidated after all fields were valid, then buttonState reverts to Disabled`() {
        makeCardFormValid()
        // Simulate a field becoming invalid: configure mock to return invalid for the next call
        every {
            mockCardValidator.validateField(eq(CardDetailsFieldType.NUMBER), any(), any())
        } returns ValidationResult(false, R.string.jp_empty)
        sut.send(CardEntryAction.CardFieldChanged(CardDetailsFieldType.NUMBER, "", FormFieldEvent.FOCUS_CHANGED))

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_pay_now))
    }

    @Test
    fun `Given shouldAskForBillingInformation=true and SubmitCardEntryForm, then Billing navigation is emitted`() =
        runTest {
            sut =
                createViewModel(
                    PaymentWidgetType.CARD_PAYMENT,
                    UiConfiguration.Builder().setShouldAskForBillingInformation(true).build(),
                )

            val navResults = collectFlow(sut.navigationEffect)

            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            assertThat(navResults.last()).isEqualTo(CardEntryNavigation.Billing)
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given shouldAskForBillingInformation=true and SubmitCardEntryForm, then payment repository is NOT called immediately`() =
        runTest {
            sut =
                createViewModel(
                    PaymentWidgetType.CARD_PAYMENT,
                    UiConfiguration.Builder().setShouldAskForBillingInformation(true).build(),
                )

            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify(exactly = 0) { cardTransactionRepository.payment(any(), any()) }
        }

    @Test
    fun `Given shouldAskForBillingInformation=true and continueButtonText, then button shows jp_continue_text`() {
        sut =
            createViewModel(
                PaymentWidgetType.CARD_PAYMENT,
                UiConfiguration.Builder().setShouldAskForBillingInformation(true).build(),
            )

        makeCardFormValid()

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
    fun `Given all billing fields are valid, then billing submit button is Enabled`() {
        makeBillingFormValid()

        val billingModel =
            sut.uiState.value
                ?.formModel
                ?.billingDetailsInputModel
        assertThat(billingModel?.submitButtonState).isEqualTo(ButtonState.Enabled(R.string.jp_pay_now))
    }

    @Test
    fun `Given billing fields are not all valid, then billing submit button is Disabled`() {
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
                createViewModel(
                    PaymentWidgetType.TOKEN_PAYMENT,
                    UiConfiguration.Builder().setShouldAskForBillingInformation(true).build(),
                )

            val navResults = collectFlow(sut.navigationEffect)

            sut.send(CardEntryAction.Initialize)
            advanceUntilIdle()

            assertThat(navResults.last()).isEqualTo(CardEntryNavigation.Billing)
        }

    @Test
    fun `Given Initialize for TOKEN_PAYMENT with no user input required, then paymentWithToken is called immediately`() =
        runTest {
            sut = createViewModel(PaymentWidgetType.TOKEN_PAYMENT)

            sut.send(CardEntryAction.Initialize)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.paymentWithToken(any(), any()) }
        }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `Given send is called with SubmitCardEntryForm action, when payment widget type is PRE_AUTH_PAYMENT_METHODS, then cardTransactionRepository's save method is called`() =
        runTest {
            sut = createViewModel(PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS)
            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            coVerify { cardTransactionRepository.save(any(), any()) }
        }

    @Test
    fun `Given avsEnabled=true and no billing information, then COUNTRY and POST_CODE fields are enabled`() {
        sut =
            createViewModel(
                PaymentWidgetType.CARD_PAYMENT,
                UiConfiguration.Builder().setAvsEnabled(true).build(),
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

            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            assertThat(results.last()).isEqualTo(error)
        }

    @Test
    fun `Given TOKEN_PAYMENT with no user input required, then uiState isUserInputRequired is false`() {
        sut = createViewModel(PaymentWidgetType.TOKEN_PAYMENT)

        assertThat(sut.uiState.value?.isUserInputRequired).isFalse()
    }

    @Test
    fun `Given CARD_PAYMENT, then uiState isUserInputRequired is true`() {
        assertThat(sut.uiState.value?.isUserInputRequired).isTrue()
    }

    @Test
    fun `Given TOKEN_PAYMENT with no enabled card fields, then billing back button is Hidden`() {
        sut = createViewModel(PaymentWidgetType.TOKEN_PAYMENT)

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
        sut = createViewModel(PaymentWidgetType.CHECK_CARD)

        val inputModel =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
        assertThat(inputModel?.actionButtonState).isEqualTo(ButtonState.Disabled(R.string.jp_check_card))
    }

    @Test
    fun `Given payment widget type is PAYMENT_METHODS and isAddingNewCard=false, then button text should be jp_pay_now`() {
        sut =
            createViewModel(
                PaymentWidgetType.PAYMENT_METHODS,
                options = CardEntryOptions(isPresentedFromPaymentMethods = true, isAddingNewCard = false),
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
                createViewModel(
                    PaymentWidgetType.PAYMENT_METHODS,
                    options = CardEntryOptions(isPresentedFromPaymentMethods = true),
                )

            val results = collectFlow(sut.cardEntryToPaymentMethodResultEffect)

            sut.send(CardEntryAction.BillingFieldChanged(BillingDetailsFieldType.EMAIL, "test@example.com", FormFieldEvent.FOCUS_CHANGED))
            sut.send(CardEntryAction.BillingFieldChanged(BillingDetailsFieldType.CITY, "London", FormFieldEvent.FOCUS_CHANGED))
            sut.send(CardEntryAction.BillingFieldChanged(BillingDetailsFieldType.POST_CODE, "W1A 1AA", FormFieldEvent.FOCUS_CHANGED))
            makeCardFormValid()
            sut.send(CardEntryAction.SubmitCardEntryForm)
            advanceUntilIdle()

            val built = results.last().build()
            assertThat(built.email).isEqualTo("test@example.com")
            assertThat(built.city).isEqualTo("London")
            assertThat(built.postalCode).isEqualTo("W1A 1AA")
        }

    @Test
    fun `Given card number is typed first while CVV is empty and untouched, then no CVV error is shown when network is detected`() {
        // Mock returns null initially so the first digit triggers networkChanged = true.
        // The CVV validator returns an invalid result for the empty field.
        every { mockCardValidator.cardNetwork } returns null
        every {
            mockCardValidator.validateField(CardDetailsFieldType.SECURITY_NUMBER, "", any())
        } returns ValidationResult(false, R.string.jp_check_cvv)

        sut.send(CardEntryAction.CardFieldChanged(CardDetailsFieldType.NUMBER, "4", FormFieldEvent.TEXT_CHANGED))

        val fieldErrors =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
                ?.fieldErrors
        assertThat(fieldErrors?.get(CardDetailsFieldType.SECURITY_NUMBER)).isNull()
    }

    @Test
    fun `Given CVV already has content when card network changes, then CVV error is surfaced`() {
        // User typed an incomplete CVV and moved focus away — error is now visible.
        every {
            mockCardValidator.validateField(CardDetailsFieldType.SECURITY_NUMBER, "12", any())
        } returns ValidationResult(false, R.string.jp_check_cvv)
        sut.send(CardEntryAction.CardFieldChanged(CardDetailsFieldType.SECURITY_NUMBER, "12", FormFieldEvent.FOCUS_CHANGED))

        // Then the user types a card number prefix that changes the detected network.
        every { mockCardValidator.cardNetwork } returns null
        every {
            mockCardValidator.validateField(CardDetailsFieldType.SECURITY_NUMBER, "12", any())
        } returns ValidationResult(false, R.string.jp_check_visa_security_code)
        sut.send(CardEntryAction.CardFieldChanged(CardDetailsFieldType.NUMBER, "41", FormFieldEvent.TEXT_CHANGED))

        val fieldErrors =
            sut.uiState.value
                ?.formModel
                ?.cardDetailsInputModel
                ?.fieldErrors
        assertThat(fieldErrors?.get(CardDetailsFieldType.SECURITY_NUMBER)).isNotNull()
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
