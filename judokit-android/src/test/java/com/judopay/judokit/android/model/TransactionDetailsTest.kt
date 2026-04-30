package com.judopay.judokit.android.model

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.model.request.Address
import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecureTwo
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Testing TransactionDetails")
internal class TransactionDetailsTest {
    private val judo: Judo = mockk(relaxed = true)
    private val transaction: Transaction = mockk(relaxed = true)
    private val threeDSecureTwo: ThreeDSecureTwo = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        mockkStatic("com.judopay.judokit.android.model.TransactionDetailsKt")
        every { transaction.toThreeDSecureTwo(any(), any()) } returns threeDSecureTwo

        every { judo.judoId } returns "100200300"
        every { judo.amount.amount } returns "1.00"
        every { judo.amount.currency } returns Currency.GBP
        every { judo.reference.paymentReference } returns "payRef"
        every { judo.reference.consumerReference } returns "consRef"
        every { judo.reference.metaData } returns null
        every { judo.address } returns null
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    @DisplayName("Builder")
    inner class BuilderTests {
        @Test
        @DisplayName("Given all fields set, build() returns TransactionDetails with those values")
        fun buildWithAllFields() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardNumber("4111111111111111")
                    .setCardHolderName("Alice Smith")
                    .setExpirationDate("12/29")
                    .setSecurityNumber("452")
                    .setCountryCode("826")
                    .setEmail("alice@example.com")
                    .setPhoneCountryCode("+44")
                    .setMobileNumber("07123456789")
                    .setAddressLine1("1 High Street")
                    .setAddressLine2("Flat 2")
                    .setAddressLine3("London")
                    .setCity("London")
                    .setPostalCode("SW1A 1AA")
                    .setAdministrativeDivision("England")
                    .setCardToken("tok_abc")
                    .setCardType(CardNetwork.VISA)
                    .setCardLastFour("1111")
                    .build()

            assertEquals("4111111111111111", details.cardNumber)
            assertEquals("Alice Smith", details.cardHolderName)
            assertEquals("12/29", details.expirationDate)
            assertEquals("452", details.securityNumber)
            assertEquals("826", details.country)
            assertEquals("alice@example.com", details.email)
            assertEquals("+44", details.phoneCountryCode)
            assertEquals("07123456789", details.mobileNumber)
            assertEquals("1 High Street", details.addressLine1)
            assertEquals("Flat 2", details.addressLine2)
            assertEquals("London", details.addressLine3)
            assertEquals("London", details.city)
            assertEquals("SW1A 1AA", details.postalCode)
            assertEquals("England", details.administrativeDivision)
            assertEquals("tok_abc", details.cardToken)
            assertEquals(CardNetwork.VISA, details.cardType)
            assertEquals("1111", details.cardLastFour)
        }

        @Test
        @DisplayName("Given email is blank, then build() sets email to null")
        fun blankEmailBecomesNull() {
            val details = TransactionDetails.Builder().setEmail("   ").build()
            assertNull(details.email)
        }

        @Test
        @DisplayName("Given email is null, then build() sets email to null")
        fun nullEmailRemainsNull() {
            val details = TransactionDetails.Builder().setEmail(null).build()
            assertNull(details.email)
        }

        @Test
        @DisplayName("Given phoneCountryCode is blank, then build() sets it to null")
        fun blankPhoneCountryCodeBecomesNull() {
            val details = TransactionDetails.Builder().setPhoneCountryCode("").build()
            assertNull(details.phoneCountryCode)
        }

        @Test
        @DisplayName("Given mobileNumber is blank, then build() sets it to null")
        fun blankMobileNumberBecomesNull() {
            val details = TransactionDetails.Builder().setMobileNumber("  ").build()
            assertNull(details.mobileNumber)
        }

        @Test
        @DisplayName("Given cardType not set and cardNumber is a VISA number, cardType is inferred as VISA")
        fun cardTypeInferredFromVisaNumber() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardNumber("4111111111111111")
                    .build()
            assertEquals(CardNetwork.VISA, details.cardType)
        }

        @Test
        @DisplayName("Given cardType is explicitly set, it is not overwritten by card number inference")
        fun explicitCardTypeNotOverwritten() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardNumber("4111111111111111")
                    .setCardType(CardNetwork.AMEX)
                    .build()
            assertEquals(CardNetwork.AMEX, details.cardType)
        }

        @Test
        @Suppress("DEPRECATION")
        @DisplayName("Given setState is called (deprecated alias), then administrativeDivision is set")
        fun setStateDeprecatedAlias() {
            val details = TransactionDetails.Builder().setState("California").build()
            assertEquals("California", details.administrativeDivision)
        }

        @Test
        @DisplayName("Given no fields set, build() returns a TransactionDetails with all null fields")
        fun buildWithNoFields() {
            val details = TransactionDetails.Builder().build()
            assertNull(details.cardNumber)
            assertNull(details.cardHolderName)
            assertNull(details.email)
        }
    }

    @Nested
    @DisplayName("getAddress")
    inner class GetAddressTests {
        @Test
        @DisplayName("When shouldAskForBillingInformation is true, returns an Address built from details")
        fun returnsBuiltAddressWhenBillingRequired() {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns true

            val details =
                TransactionDetails
                    .Builder()
                    .setAddressLine1("1 High Street")
                    .setCity("London")
                    .setPostalCode("SW1A 1AA")
                    .setCountryCode("826")
                    .setAdministrativeDivision("England")
                    .build()

            val address = details.getAddress(judo)

            assertThat(address).isNotNull()
        }

        @Test
        @DisplayName("When shouldAskForBillingInformation is false, returns judo.address")
        fun returnsJudoAddressWhenBillingNotRequired() {
            val expected: Address = mockk(relaxed = true)
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.address } returns expected

            val details = TransactionDetails.Builder().build()

            assertEquals(expected, details.getAddress(judo))
        }

        @Test
        @DisplayName("When shouldAskForBillingInformation is false and judo.address is null, returns null")
        fun returnsNullWhenJudoAddressIsNull() {
            every { judo.uiConfiguration.shouldAskForBillingInformation } returns false
            every { judo.address } returns null

            val details = TransactionDetails.Builder().build()

            assertNull(details.getAddress(judo))
        }
    }

    @Nested
    @DisplayName("toReceipt")
    inner class ToReceiptTests {
        @Test
        @DisplayName("toReceipt maps payment reference and currency from judo")
        fun toReceiptMapsJudoFields() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardLastFour("4242")
                    .setCardToken("tok_123")
                    .setCardType(CardNetwork.VISA)
                    .build()

            val receipt = details.toReceipt(judo)

            assertEquals("payRef", receipt.yourPaymentReference)
            assertEquals("GBP", receipt.currency)
        }

        @Test
        @DisplayName("toReceipt maps card details from TransactionDetails")
        fun toReceiptMapsCardDetails() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardLastFour("4242")
                    .setCardToken("tok_123")
                    .setCardType(CardNetwork.VISA)
                    .build()

            val receipt = details.toReceipt(judo)

            assertThat(receipt.cardDetails?.lastFour).isEqualTo("4242")
            assertThat(receipt.cardDetails?.token).isEqualTo("tok_123")
            assertThat(receipt.cardDetails?.type).isEqualTo(CardNetwork.VISA.typeId)
        }

        @Test
        @DisplayName("toReceipt uses -1 for type when cardType is null")
        fun toReceiptUsesMinus1WhenNoCardType() {
            val details =
                TransactionDetails
                    .Builder()
                    .build()

            val receipt = details.toReceipt(judo)

            assertThat(receipt.cardDetails?.type).isEqualTo(-1)
        }

        @Test
        @DisplayName("toReceipt strips non-digits from judoId to build receipt judoId")
        fun toReceiptStripsNonDigitsFromJudoId() {
            every { judo.judoId } returns "100-200-300"

            val details = TransactionDetails.Builder().build()
            val receipt = details.toReceipt(judo)

            assertEquals(100200300L, receipt.judoId)
        }
    }

    @Nested
    @DisplayName("toSaveCardRequest")
    inner class ToSaveCardRequestTests {
        @Test
        @DisplayName("toSaveCardRequest maps card fields correctly")
        fun toSaveCardRequestMapsFields() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardNumber("4111111111111111")
                    .setExpirationDate("12/29")
                    .setSecurityNumber("452")
                    .setCardHolderName("Alice")
                    .build()

            val request = details.toSaveCardRequest(judo, transaction)

            val json = Gson().toJson(request)
            assertThat(request).isNotNull()
            assertThat(json).contains("4111111111111111")
            assertThat(json).contains("12/29")
            assertThat(json).contains("452")
            assertThat(json).contains("Alice")
        }

        @Test
        @DisplayName("toSaveCardRequest with null cardHolderName builds successfully")
        fun toSaveCardRequestWithNullCardHolderName() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardNumber("4111111111111111")
                    .setExpirationDate("12/29")
                    .setSecurityNumber("452")
                    .build()
            val request = details.toSaveCardRequest(judo, transaction)
            assertThat(request).isNotNull()
        }
    }

    @Nested
    @DisplayName("to*Request with ThreeDSecureTwo")
    inner class ToRequestWithThreeDSTests {
        @Test
        @DisplayName("toPaymentRequest builds successfully and delegates ThreeDSecureTwo to transaction")
        fun toPaymentRequestBuildsSuccessfully() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardNumber("4111111111111111")
                    .setExpirationDate("12/29")
                    .setSecurityNumber("452")
                    .setCardHolderName("Alice")
                    .build()

            val request = details.toPaymentRequest(judo, transaction)

            assertThat(request).isNotNull()
        }

        @Test
        @DisplayName("toPaymentRequest with overrides builds successfully")
        fun toPaymentRequestWithOverridesBuildsSuccessfully() {
            val overrides =
                TransactionDetailsOverrides(
                    softDeclineReceiptId = "rcpt_123",
                    challengeRequestIndicator = ChallengeRequestIndicator.CHALLENGE_AS_MANDATE,
                )
            val details =
                TransactionDetails
                    .Builder()
                    .setCardNumber("4111111111111111")
                    .setExpirationDate("12/29")
                    .setSecurityNumber("452")
                    .build()

            val request = details.toPaymentRequest(judo, transaction, overrides)

            assertThat(request).isNotNull()
        }

        @Test
        @DisplayName("toPreAuthRequest builds successfully")
        fun toPreAuthRequestBuildsSuccessfully() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardNumber("4111111111111111")
                    .setExpirationDate("12/29")
                    .setSecurityNumber("452")
                    .build()

            val request = details.toPreAuthRequest(judo, transaction)

            assertThat(request).isNotNull()
        }

        @Test
        @DisplayName("toCheckCardRequest builds successfully")
        fun toCheckCardRequestBuildsSuccessfully() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardNumber("4111111111111111")
                    .setExpirationDate("12/29")
                    .setSecurityNumber("452")
                    .build()

            val request = details.toCheckCardRequest(judo, transaction)

            assertThat(request).isNotNull()
        }

        @Test
        @DisplayName("toTokenRequest builds successfully")
        fun toTokenRequestBuildsSuccessfully() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardToken("tok_abc")
                    .setCardType(CardNetwork.VISA)
                    .build()

            val request = details.toTokenRequest(judo, transaction)

            assertThat(request).isNotNull()
        }

        @Test
        @DisplayName("toPreAuthTokenRequest builds successfully")
        fun toPreAuthTokenRequestBuildsSuccessfully() {
            val details =
                TransactionDetails
                    .Builder()
                    .setCardToken("tok_abc")
                    .setCardType(CardNetwork.VISA)
                    .build()

            val request = details.toPreAuthTokenRequest(judo, transaction)

            assertThat(request).isNotNull()
        }
    }
}
