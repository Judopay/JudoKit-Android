package com.judopay.judokit.android

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.judopay.judokit.android.api.model.request.Address
import com.judopay.judokit.android.api.model.request.BankSaleRequest
import com.judopay.judokit.android.api.model.request.CheckCardRequest
import com.judopay.judokit.android.api.model.request.IdealSaleRequest
import com.judopay.judokit.android.api.model.request.PaymentRequest
import com.judopay.judokit.android.api.model.request.RegisterCardRequest
import com.judopay.judokit.android.api.model.request.TokenRequest
import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecureTwo
import com.judopay.judokit.android.model.ApiEnvironment
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.PrimaryAccountDetails
import com.judopay.judokit.android.ui.common.parcelable
import com.judopay.judokit.android.ui.error.JudoNotProvidedError
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Test com.judopay.judokit.android.JudoExtensions")
internal class JudoExtensionsTest {

    private val mockAddress: Address = mockk()
    private val mockThreeDSecureTwo: ThreeDSecureTwo = mockk()
    private val mockPrimaryAccountDetails: PrimaryAccountDetails = mockk()
    val judo: Judo = mockk(relaxed = true) {
        every { judoId } returns "123456789"
        every { amount } returns mockk(relaxed = true) {
            every { currency } returns Currency.GBP
            every { amount } returns "1"
        }
        every { reference } returns mockk(relaxed = true) {
            every { consumerReference } returns "ref"
            every { paymentReference } returns "ref"
        }
        every { primaryAccountDetails } returns mockPrimaryAccountDetails
        every { address } returns mockAddress
        every { initialRecurringPayment } returns false
        every { pbbaConfiguration } returns mockk(relaxed = true) {
            every { mobileNumber } returns "1"
            every { emailAddress } returns "email"
            every { appearsOnStatement } returns "appearsOnStatement"
            every { deepLinkScheme } returns "redirectUrl"
        }
    }

    @DisplayName("Given judo.isSandboxed is true, then return ApiEnvironment.SANDBOX.host")
    @Test
    fun returnSandboxHostWhenIsSandboxedTrue() {
        val judo: Judo = mockk(relaxed = true) {
            every { isSandboxed } returns true
        }
        assertEquals(ApiEnvironment.SANDBOX.host, judo.apiBaseUrl)
    }

    @DisplayName("Given judo.isSandboxed is false, then return ApiEnvironment.LIVE.host")
    @Test
    fun returnLiveHostWhenIsSandboxedFalse() {
        val judo: Judo = mockk(relaxed = true) {
            every { isSandboxed } returns false
        }
        assertEquals(ApiEnvironment.LIVE.host, judo.apiBaseUrl)
    }

    @DisplayName("Given requireNotNullOrEmpty is called , when value parameter is null, then throw IllegalArgumentException")
    @Test
    fun throwsExceptionWhenRequireNotNullOrEmptyValueNull() {
        assertThrows<IllegalArgumentException> { requireNotNullOrEmpty(null, "propertyName") }
    }

    @DisplayName("Given requireNotNullOrEmpty is called, when value parameter is empty, then throw IllegalArgumentException")
    @Test
    fun throwsExceptionWhenRequireNotNullOrEmptyValueEmpty() {
        assertThrows<IllegalArgumentException> { requireNotNullOrEmpty("", "propertyName") }
    }

    @DisplayName("Given requireNotNullOrEmpty is called, when value parameter is valid, then return value")
    @Test
    fun returnValueWhenRequireNotNullOrEmptyValueValid() {
        assertEquals("Value", requireNotNullOrEmpty("Value", "PropertyName"))
    }

    @DisplayName("Given requireNotNull is called, when value parameter is null, then throw IllegalArgumentException")
    @Test
    fun throwsExceptionWhenRequireNotNullValueNull() {
        assertThrows<IllegalArgumentException> { requireNotNull(null, "propertyName") }
    }

    @DisplayName("Given requireNotNull is called, when value parameter is valid, then return value")
    @Test
    fun returnValueWhenRequireNotNullValueValid() {
        val any: Any? = Any()
        assertEquals(any, requireNotNull(any), "propertyName")
    }

    @DisplayName("Given view.parentOfType is called, when parameter is LinearLayout::class, then return view's parent LinearLayout")
    @Test
    fun returnViewParent() {
        val expectedParent: LinearLayout = mockk(relaxed = true)
        val view: View = mockk(relaxed = true) {
            every { parent } returns expectedParent
        }

        assertEquals(expectedParent, view.parentOfType(LinearLayout::class.java))
    }

    @DisplayName("Given view.parentOfType is called, when view's parent is not of parentType, then return null")
    @Test
    fun returnNullOnViewsParentNotParentType() {
        val expectedParent: LinearLayout = mockk(relaxed = true)
        val view: View = mockk(relaxed = true) {
            every { parent } returns expectedParent
        }

        assertEquals(null, view.parentOfType(ViewGroup::class.java))
    }

    @DisplayName("Given view.parentOfType is called, when view's parent is not a View, then return null")
    @Test
    fun returnNullOnViewsParentNotView() {
        val expectedParent: ViewGroup = mockk(relaxed = true)
        val view: View = mockk(relaxed = true) {
            every { parent } returns expectedParent
        }

        assertEquals(null, view.parentOfType(ViewGroup::class.java))
    }

    @DisplayName("Given FragmentActivity.judo is called, then get judo object")
    @Test
    fun getJudoObjectOnFragmentActivityJudoCall() {
        val expectedJudoObject: Judo = mockk(relaxed = true)
        val fragment: FragmentActivity = mockk(relaxed = true) {
            every { intent.parcelable<Judo>(JUDO_OPTIONS) } returns expectedJudoObject
        }

        assertEquals(expectedJudoObject, fragment.judo)
    }

    @DisplayName("Given FragmentActivity.judo is called, when judo object is null, then throw JudoNotProvidedError")
    @Test
    fun throwJudoNotProvidedErrorOnJudoObjectNull() {
        val fragment: FragmentActivity = mockk(relaxed = true) {
            every { intent.parcelable<Judo>(JUDO_OPTIONS) } returns null
        }

        assertThrows<JudoNotProvidedError> { fragment.judo }
    }

    @DisplayName("Given Fragment.judo is called, then get judo object")
    @Test
    fun getJudoObjectOnFragmentJudoCall() {
        val expectedJudoObject: Judo = mockk(relaxed = true)
        val fragmentActivity: FragmentActivity = mockk(relaxed = true) {
            every { intent.parcelable<Judo>(JUDO_OPTIONS) } returns expectedJudoObject
        }
        val fragment: Fragment = mockk(relaxed = true) {
            every { requireActivity() } returns fragmentActivity
        }

        assertEquals(expectedJudoObject, fragment.judo)
    }

    @DisplayName("Given String.withWhitespacesRemoved is called, then return string with no whitespace")
    @Test
    fun returnStringWithNoWhitespace() {
        val whitespaceString = "White space"

        assertEquals("Whitespace", whitespaceString.withWhitespacesRemoved)
    }

    @DisplayName("Given Judo.toIdealSaleRequest is called, then map Judo to IdealSaleRequest")
    @Test
    fun mapJudoToIdealSaleRequest() {
        val expected = IdealSaleRequest.Builder().setAmount("1")
            .setMerchantConsumerReference("ref")
            .setMerchantPaymentReference("ref")
            .setJudoId("123456789")
            .setBic("bic")
            .setPaymentMetadata(emptyMap())
            .build()

        val actual = judo.toIdealSaleRequest("bic")

        assertEquals(expected, actual)
    }

    @DisplayName("Given Judo.toBankSaleRequest is called, then map Judo to BankSaleRequest")
    @Test
    fun mapJudoToBankSaleRequest() {
        val expected = BankSaleRequest.Builder().setAmount("1")
            .setMerchantConsumerReference("ref")
            .setMerchantPaymentReference("ref")
            .setJudoId("123456789")
            .setPaymentMetadata(emptyMap())
            .setMobileNumber("1")
            .setEmailAddress("email")
            .setAppearsOnStatement("appearsOnStatement")
            .setMerchantRedirectUrl("redirectUrl")
            .build()

        val actual = judo.toBankSaleRequest()

        assertEquals(expected, actual)
    }

    @DisplayName("Given Judo.toPaymentRequest is called, then map Judo to PaymentRequest")
    @Test
    fun mapJudoToPaymentRequest() {
        val expected = PaymentRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference("ref")
            .setJudoId("123456789")
            .setYourPaymentMetaData(emptyMap())
            .setAmount("1")
            .setCurrency(Currency.GBP.name)
            .setJudoId("123456789")
            .setYourConsumerReference("ref")
            .setAddress(mockAddress)
            .setCardNumber("4111111111111111")
            .setCv2("452")
            .setExpiryDate("1229")
            .setPrimaryAccountDetails(mockPrimaryAccountDetails)
            .setInitialRecurringPayment(false)
            .setThreeDSecure(mockThreeDSecureTwo)
            .setMobileNumber("321321321")
            .setPhoneCountryCode("+44")
            .build()

        val actual = judo.toPaymentRequest(
            "4111111111111111",
            "1229",
            "452",
            mockThreeDSecureTwo,
            "321321321",
            "44"
        )

        val gson = Gson()
        assertEquals(gson.toJson(expected), gson.toJson(actual))
    }

    @DisplayName("Given Judo.toPaymentRequest is called with unusual phone details, then map Judo to PaymentRequest with phone details parsed correctly")
    @Test
    fun mapJudoToPaymentRequestWithUnusualPhoneDetails() {
        val expected = PaymentRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference("ref")
            .setJudoId("123456789")
            .setYourPaymentMetaData(emptyMap())
            .setAmount("1")
            .setCurrency(Currency.GBP.name)
            .setJudoId("123456789")
            .setYourConsumerReference("ref")
            .setAddress(mockAddress)
            .setCardNumber("4111111111111111")
            .setCv2("452")
            .setExpiryDate("1229")
            .setPrimaryAccountDetails(mockPrimaryAccountDetails)
            .setInitialRecurringPayment(false)
            .setThreeDSecure(mockThreeDSecureTwo)
            .setMobileNumber("(321)321-321")
            .setPhoneCountryCode("+1754")
            .build()

        val actual = judo.toPaymentRequest(
            "4111111111111111",
            "1229",
            "452",
            mockThreeDSecureTwo,
            "754321321321",
            "1"
        )

        val gson = Gson()
        assertEquals(gson.toJson(expected), gson.toJson(actual))
    }

    @DisplayName("Given Judo.toRegisterCardRequest is called, then map Judo to RegisterCardRequest")
    @Test
    fun mapJudoToRegisterCardRequest() {
        val expected = RegisterCardRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference("ref")
            .setJudoId("123456789")
            .setYourPaymentMetaData(emptyMap())
            .setAmount("1")
            .setCurrency(Currency.GBP.name)
            .setJudoId("123456789")
            .setYourConsumerReference("ref")
            .setAddress(mockAddress)
            .setCardNumber("4111111111111111")
            .setCv2("452")
            .setExpiryDate("1229")
            .setPrimaryAccountDetails(mockPrimaryAccountDetails)
            .setInitialRecurringPayment(false)
            .setThreeDSecure(mockThreeDSecureTwo)
            .build()

        val actual = judo.toRegisterCardRequest("4111111111111111", "1229", "452", mockThreeDSecureTwo)

        val gson = Gson()
        assertEquals(gson.toJson(expected), gson.toJson(actual))
    }

    @DisplayName("Given Judo.toSaveCardRequest is called, then map Judo to CheckCardRequest")
    @Test
    fun mapJudoToCheckCardRequest() {
        val expected = CheckCardRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference("ref")
            .setJudoId("123456789")
            .setYourPaymentMetaData(emptyMap())
            .setCurrency(Currency.GBP.name)
            .setJudoId("123456789")
            .setYourConsumerReference("ref")
            .setAddress(mockAddress)
            .setCardNumber("4111111111111111")
            .setCv2("452")
            .setExpiryDate("1229")
            .setPrimaryAccountDetails(mockPrimaryAccountDetails)
            .setInitialRecurringPayment(false)
            .setThreeDSecure(mockThreeDSecureTwo)
            .build()

        val actual = judo.toCheckCardRequest("4111111111111111", "1229", "452", mockThreeDSecureTwo)

        val gson = Gson()
        assertEquals(gson.toJson(expected), gson.toJson(actual))
    }

    @DisplayName("Given Judo.toTokenRequest is called, then map Judo to TokenRequest")
    @Test
    fun mapJudoToTokenRequest() {
        val expected = TokenRequest.Builder()
            .setAmount("1")
            .setUniqueRequest(false)
            .setYourPaymentReference("ref")
            .setJudoId("123456789")
            .setYourPaymentMetaData(emptyMap())
            .setCurrency(Currency.GBP.name)
            .setJudoId("123456789")
            .setYourConsumerReference("ref")
            .setAddress(mockAddress)
            .setCardToken("cardToken")
            .setCv2("452")
            .setPrimaryAccountDetails(mockPrimaryAccountDetails)
            .setInitialRecurringPayment(false)
            .setThreeDSecure(mockThreeDSecureTwo)
            .build()

        val actual = judo.toTokenRequest("cardToken", mockThreeDSecureTwo, "452")

        val gson = Gson()
        assertEquals(gson.toJson(expected), gson.toJson(actual))
    }
}
