package com.judopay.judokit.android

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.judopay.judokit.android.api.model.request.Address
import com.judopay.judokit.android.api.model.request.CheckCardRequest
import com.judopay.judokit.android.api.model.request.GooglePayRequest
import com.judopay.judokit.android.api.model.request.GooglePayWallet
import com.judopay.judokit.android.api.model.request.PaymentRequest
import com.judopay.judokit.android.api.model.request.SaveCardRequest
import com.judopay.judokit.android.api.model.request.TokenRequest
import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecureTwo
import com.judopay.judokit.android.model.ApiEnvironment
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.PrimaryAccountDetails
import com.judopay.judokit.android.model.googlepay.GooglePayAddress
import com.judopay.judokit.android.ui.common.parcelable
import com.judopay.judokit.android.ui.error.JudoNotProvidedError
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Suppress("LargeClass")
@DisplayName("Test com.judopay.judokit.android.JudoExtensions")
internal class JudoExtensionsTest {
    private val mockAddress: Address = mockk()
    private val mockThreeDSecureTwo: ThreeDSecureTwo = mockk()
    private val mockPrimaryAccountDetails: PrimaryAccountDetails = mockk()

    private fun buildJudo(
        metaData: Bundle? = null,
        address: Address? = mockAddress,
    ): Judo =
        mockk(relaxed = true) {
            every { judoId } returns "123456789"
            every { amount } returns
                mockk(relaxed = true) {
                    every { currency } returns Currency.GBP
                    every { amount } returns "1"
                }
            every { reference } returns
                mockk(relaxed = true) {
                    every { consumerReference } returns "ref"
                    every { paymentReference } returns "ref"
                    if (metaData != null) every { this@mockk.metaData } returns metaData
                }
            every { primaryAccountDetails } returns mockPrimaryAccountDetails
            every { this@mockk.address } returns address
            every { initialRecurringPayment } returns false
        }

    val judo: Judo = buildJudo()

    private val metadataBundle =
        mockk<Bundle> {
            every { keySet() } returns setOf("key1")
            every { getString("key1") } returns "val1"
        }

    private val judoWithMetaData: Judo = buildJudo(metaData = metadataBundle)

    private val judoNoAddress: Judo = buildJudo(address = null)

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @DisplayName("Given judo.isSandboxed is true, then return ApiEnvironment.SANDBOX.host")
    @Test
    fun returnSandboxHostWhenIsSandboxedTrue() {
        val judo: Judo =
            mockk(relaxed = true) {
                every { isSandboxed } returns true
            }
        assertEquals(ApiEnvironment.SANDBOX.host, judo.apiBaseUrl)
    }

    @DisplayName("Given judo.isSandboxed is false, then return ApiEnvironment.LIVE.host")
    @Test
    fun returnLiveHostWhenIsSandboxedFalse() {
        val judo: Judo =
            mockk(relaxed = true) {
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
        val view: View =
            mockk(relaxed = true) {
                every { parent } returns expectedParent
            }

        assertEquals(expectedParent, view.parentOfType(LinearLayout::class.java))
    }

    @DisplayName("Given view.parentOfType is called, when view's parent is not of parentType, then return null")
    @Test
    fun returnNullOnViewsParentNotParentType() {
        val expectedParent: LinearLayout = mockk(relaxed = true)
        val view: View =
            mockk(relaxed = true) {
                every { parent } returns expectedParent
            }

        assertEquals(null, view.parentOfType(ViewGroup::class.java))
    }

    @DisplayName("Given view.parentOfType is called, when view's parent is not a View, then return null")
    @Test
    fun returnNullOnViewsParentNotView() {
        val expectedParent: ViewGroup = mockk(relaxed = true)
        val view: View =
            mockk(relaxed = true) {
                every { parent } returns expectedParent
            }

        assertEquals(null, view.parentOfType(ViewGroup::class.java))
    }

    @DisplayName("Given FragmentActivity.judo is called, then get judo object")
    @Test
    fun getJudoObjectOnFragmentActivityJudoCall() {
        val expectedJudoObject: Judo = mockk(relaxed = true)
        val fragment: FragmentActivity =
            mockk(relaxed = true) {
                every { intent.parcelable<Judo>(JUDO_OPTIONS) } returns expectedJudoObject
            }

        assertEquals(expectedJudoObject, fragment.judo)
    }

    @DisplayName("Given FragmentActivity.judo is called, when judo object is null, then throw JudoNotProvidedError")
    @Test
    fun throwJudoNotProvidedErrorOnJudoObjectNull() {
        val fragment: FragmentActivity =
            mockk(relaxed = true) {
                every { intent.parcelable<Judo>(JUDO_OPTIONS) } returns null
            }

        assertThrows<JudoNotProvidedError> { fragment.judo }
    }

    @DisplayName("Given Fragment.judo is called, then get judo object")
    @Test
    fun getJudoObjectOnFragmentJudoCall() {
        val expectedJudoObject: Judo = mockk(relaxed = true)
        val fragmentActivity: FragmentActivity =
            mockk(relaxed = true) {
                every { intent.parcelable<Judo>(JUDO_OPTIONS) } returns expectedJudoObject
            }
        val fragment: Fragment =
            mockk(relaxed = true) {
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

    @DisplayName("Given Judo.toPaymentRequest is called, then map Judo to PaymentRequest")
    @Test
    fun mapJudoToPaymentRequest() {
        val expected =
            PaymentRequest
                .Builder()
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
                .build()

        val actual =
            judo.toPaymentRequest(
                "4111111111111111",
                "1229",
                "452",
                mockThreeDSecureTwo,
                "321321321",
            )

        val gson = Gson()
        assertEquals(gson.toJson(expected), gson.toJson(actual))
    }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given Judo.toPaymentRequest is called with unusual phone details, then map Judo to PaymentRequest with phone details parsed correctly",
    )
    @Test
    fun mapJudoToPaymentRequestWithUnusualPhoneDetails() {
        val expected =
            PaymentRequest
                .Builder()
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

        val actual =
            judo.toPaymentRequest(
                "4111111111111111",
                "1229",
                "452",
                mockThreeDSecureTwo,
                "754321321321",
                "1",
            )

        val gson = Gson()
        assertEquals(gson.toJson(expected), gson.toJson(actual))
    }

    @DisplayName("Given Judo.toSaveCardRequest is called, then map Judo to CheckCardRequest")
    @Test
    fun mapJudoToCheckCardRequest() {
        val expected =
            CheckCardRequest
                .Builder()
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
        val expected =
            TokenRequest
                .Builder()
                .setAmount("1")
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

    @Nested
    @DisplayName("validateTimeout")
    inner class ValidateTimeoutTests {
        @DisplayName("Given null timeout, then return minTimeout as default")
        @Test
        fun returnsMinTimeoutWhenNull() {
            assertEquals(30L, validateTimeout(null, "timeout", 30L, 600L))
        }

        @DisplayName("Given timeout within range, then return the timeout value")
        @Test
        fun returnsValueWhenInRange() {
            assertEquals(120L, validateTimeout(120L, "timeout", 30L, 600L))
        }

        @DisplayName("Given timeout below minimum, then throw IllegalArgumentException")
        @Test
        fun throwsWhenBelowMin() {
            assertThrows<IllegalArgumentException> { validateTimeout(5L, "timeout", 30L, 600L) }
        }

        @DisplayName("Given timeout above maximum, then throw IllegalArgumentException")
        @Test
        fun throwsWhenAboveMax() {
            assertThrows<IllegalArgumentException> { validateTimeout(700L, "timeout", 30L, 600L) }
        }

        @DisplayName("Given timeout equal to minimum, then return the value")
        @Test
        fun returnsValueWhenEqualToMin() {
            assertEquals(30L, validateTimeout(30L, "timeout", 30L, 600L))
        }

        @DisplayName("Given timeout equal to maximum, then return the value")
        @Test
        fun returnsValueWhenEqualToMax() {
            assertEquals(600L, validateTimeout(600L, "timeout", 30L, 600L))
        }
    }

    @DisplayName("Given requireNotNullOrEmpty with custom message, when null, then exception message matches")
    @Test
    fun customMessageUsedWhenRequireNotNullOrEmptyNull() {
        val ex =
            assertThrows<IllegalArgumentException> {
                requireNotNullOrEmpty(null, "field", "custom null message")
            }
        assertEquals("custom null message", ex.message)
    }

    @DisplayName("Given requireNotNullOrEmpty with custom message, when empty, then exception message matches")
    @Test
    fun customMessageUsedWhenRequireNotNullOrEmptyEmpty() {
        val ex =
            assertThrows<IllegalArgumentException> {
                requireNotNullOrEmpty("", "field", "custom empty message")
            }
        assertEquals("custom empty message", ex.message)
    }

    @DisplayName("Given requireNotNullOrEmpty with no message, when null, then default message contains property name")
    @Test
    fun defaultMessageContainsPropertyNameWhenNull() {
        val ex =
            assertThrows<IllegalArgumentException> {
                requireNotNullOrEmpty(null, "myField")
            }
        assertTrue(ex.message?.contains("myField") == true)
    }

    @DisplayName("Given requireNotNull with custom message, when null, then exception message matches")
    @Test
    fun customMessageUsedWhenRequireNotNullNull() {
        val ex =
            assertThrows<IllegalArgumentException> {
                requireNotNull(null, "field", "custom message")
            }
        assertEquals("custom message", ex.message)
    }

    @DisplayName("Given requireNotNull with no message, when null, then default message contains property name")
    @Test
    fun defaultMessageContainsPropertyNameForRequireNotNull() {
        val ex =
            assertThrows<IllegalArgumentException> {
                requireNotNull(null, "myProp")
            }
        assertTrue(ex.message?.contains("myProp") == true)
    }

    @DisplayName("Given trimIndent(true), then newlines are replaced with single spaces")
    @Test
    fun trimIndentTrueReplacesNewlinesWithSpaces() {
        val input = """
            hello
            world
        """
        val result = input.trimIndent(true)
        assertTrue(!result.contains('\n'))
        assertTrue(result.contains("hello world"))
    }

    @DisplayName("Given trimIndent(false), then newlines are preserved")
    @Test
    fun trimIndentFalsePreservesNewlines() {
        val input = """
            hello
            world
        """
        val result = input.trimIndent(false)
        assertTrue(result.contains('\n'))
    }

    @DisplayName("Given trimIndent(true) with extra spaces, then consecutive whitespace is collapsed")
    @Test
    fun trimIndentTrueCollapsesExtraWhitespace() {
        val result = "a\nb".trimIndent(true)
        assertEquals("a b", result)
    }

    @DisplayName("Given toJSONString is called on a data object, then returns valid JSON")
    @Test
    fun toJSONStringReturnsValidJson() {
        data class Sample(
            val name: String,
            val value: Int,
        )
        val obj = Sample("test", 42)
        val json = obj.toJSONString()
        assertEquals("""{"name":"test","value":42}""", json)
    }

    @DisplayName("Given Bundle with string values, then toMap includes them")
    @Test
    fun bundleToMapIncludesStringValues() {
        val bundle =
            mockk<Bundle> {
                every { keySet() } returns setOf("key1", "key2")
                every { getString("key1") } returns "value1"
                every { getString("key2") } returns "value2"
            }
        assertEquals(mapOf("key1" to "value1", "key2" to "value2"), bundle.toMap())
    }

    @DisplayName("Given Bundle with a null value, then toMap excludes that entry")
    @Test
    fun bundleToMapExcludesNullValues() {
        val bundle =
            mockk<Bundle> {
                every { keySet() } returns setOf("key1", "nullKey")
                every { getString("key1") } returns "value1"
                every { getString("nullKey") } returns null
            }
        val result = bundle.toMap()
        assertEquals(mapOf("key1" to "value1"), result)
        assertTrue(!result.containsKey("nullKey"))
    }

    @DisplayName("Given empty Bundle, then toMap returns empty map")
    @Test
    fun emptyBundleToMapReturnsEmptyMap() {
        val bundle =
            mockk<Bundle> {
                every { keySet() } returns emptySet()
            }
        assertEquals(emptyMap<String, String>(), bundle.toMap())
    }

    @DisplayName("Given string with tabs, then withWhitespacesRemoved removes them")
    @Test
    fun withWhitespacesRemovedRemovesTabs() {
        assertEquals("abc", "a\tb\tc".withWhitespacesRemoved)
    }

    @DisplayName("Given string with multiple spaces, then withWhitespacesRemoved removes all")
    @Test
    fun withWhitespacesRemovedRemovesMultipleSpaces() {
        assertEquals("abc", "a  b  c".withWhitespacesRemoved)
    }

    @DisplayName("Given empty string, then withWhitespacesRemoved returns empty string")
    @Test
    fun withWhitespacesRemovedHandlesEmptyString() {
        assertEquals("", "".withWhitespacesRemoved)
    }

    @DisplayName("Given Judo.toSaveCardRequest is called without cardHolderName, then map Judo to SaveCardRequest")
    @Test
    fun mapJudoToSaveCardRequest() {
        val expected =
            SaveCardRequest
                .Builder()
                .setYourPaymentReference("ref")
                .setCurrency(Currency.GBP.name)
                .setJudoId("123456789")
                .setYourConsumerReference("ref")
                .setYourPaymentMetaData(emptyMap())
                .setAddress(mockAddress)
                .setCardNumber("4111111111111111")
                .setExpiryDate("1229")
                .setCv2("452")
                .setPrimaryAccountDetails(mockPrimaryAccountDetails)
                .build()

        val actual = judo.toSaveCardRequest("4111111111111111", "1229", "452")

        assertEquals(Gson().toJson(expected), Gson().toJson(actual))
    }

    @DisplayName("Given Judo.toSaveCardRequest is called with cardHolderName, then cardHolderName is set")
    @Test
    fun mapJudoToSaveCardRequestWithCardHolderName() {
        val expected =
            SaveCardRequest
                .Builder()
                .setYourPaymentReference("ref")
                .setCurrency(Currency.GBP.name)
                .setJudoId("123456789")
                .setYourConsumerReference("ref")
                .setYourPaymentMetaData(emptyMap())
                .setAddress(mockAddress)
                .setCardNumber("4111111111111111")
                .setExpiryDate("1229")
                .setCv2("452")
                .setCardHolderName("Alice")
                .setPrimaryAccountDetails(mockPrimaryAccountDetails)
                .build()

        val actual = judo.toSaveCardRequest("4111111111111111", "1229", "452", "Alice")

        assertEquals(Gson().toJson(expected), Gson().toJson(actual))
    }

    @DisplayName("Given Judo.toGooglePayRequest is called, then map Judo to GooglePayRequest")
    @Test
    fun mapJudoToGooglePayRequest() {
        val wallet =
            GooglePayWallet
                .Builder()
                .setToken("token123")
                .setCardNetwork("VISA")
                .setCardDetails("1234")
                .setBillingAddress(null)
                .build()

        val expected =
            GooglePayRequest
                .Builder()
                .setJudoId("123456789")
                .setAmount("1")
                .setCurrency(Currency.GBP.name)
                .setYourPaymentReference("ref")
                .setYourConsumerReference("ref")
                .setYourPaymentMetaData(emptyMap())
                .setPrimaryAccountDetails(mockPrimaryAccountDetails)
                .setCardAddress(mockAddress)
                .setGooglePayWallet(wallet)
                .build()

        val actual = judo.toGooglePayRequest("VISA", "1234", "token123")

        assertEquals(Gson().toJson(expected), Gson().toJson(actual))
    }

    @DisplayName("Given Judo.toGooglePayRequest with billingAddress, then billingAddress is set in wallet")
    @Test
    fun mapJudoToGooglePayRequestWithBillingAddress() {
        val billingAddress = mockk<GooglePayAddress>(relaxed = true)

        val actual = judo.toGooglePayRequest("VISA", "1234", "token123", billingAddress)

        assertEquals(Gson().toJson(billingAddress), Gson().toJson(actual.googlePayWallet.billingAddress))
    }

    @DisplayName("Given Judo.toTokenRequest with null securityCode, then cv2 is absent from request")
    @Test
    fun mapJudoToTokenRequestWithNullSecurityCode() {
        val actual = judo.toTokenRequest("cardToken", mockThreeDSecureTwo, null)
        assertFalse(Gson().toJson(actual).contains("cv2"))
    }

    @DisplayName("Given view.parentOfType is called and target is a grandparent, then return it")
    @Test
    fun returnGrandparentFromParentOfType() {
        val grandparent: LinearLayout = mockk(relaxed = true)
        val directParent: ViewGroup =
            mockk(relaxed = true) {
                every { parent } returns grandparent
            }
        val view: View =
            mockk(relaxed = true) {
                every { parent } returns directParent
            }

        assertEquals(grandparent, view.parentOfType(LinearLayout::class.java))
    }

    @DisplayName("Given view.parentOfType when no ancestor matches, then return null")
    @Test
    fun returnNullWhenNoAncestorMatchesParentOfType() {
        val grandparent: ViewGroup =
            mockk(relaxed = true) {
                every { parent } returns null
            }
        val directParent: ViewGroup =
            mockk(relaxed = true) {
                every { parent } returns grandparent
            }
        val view: View =
            mockk(relaxed = true) {
                every { parent } returns directParent
            }

        assertNull(view.parentOfType(LinearLayout::class.java))
    }

    @DisplayName("Given subViewsWithType with a matching direct child, then return it")
    @Test
    fun subViewsWithTypeReturnsMatchingDirectChild() {
        val child: LinearLayout = mockk(relaxed = true)
        val parent: ViewGroup =
            mockk(relaxed = true) {
                every { childCount } returns 1
                every { getChildAt(0) } returns child
            }

        val result = parent.subViewsWithType(LinearLayout::class.java)
        assertEquals(listOf(child), result)
    }

    @DisplayName("Given subViewsWithType with no matching children, then return empty list")
    @Test
    fun subViewsWithTypeReturnsEmptyListWhenNoMatch() {
        val child: ViewGroup =
            mockk(relaxed = true) {
                every { childCount } returns 0
            }
        val parent: ViewGroup =
            mockk(relaxed = true) {
                every { childCount } returns 1
                every { getChildAt(0) } returns child
            }

        val result = parent.subViewsWithType(LinearLayout::class.java)
        assertTrue(result.isEmpty())
    }

    @DisplayName("Given subViewsWithType with a matching nested child, then return it")
    @Test
    fun subViewsWithTypeReturnsNestedMatch() {
        val deepChild: LinearLayout = mockk(relaxed = true)
        val nestedGroup: ViewGroup =
            mockk(relaxed = true) {
                every { childCount } returns 1
                every { getChildAt(0) } returns deepChild
            }
        val parent: ViewGroup =
            mockk(relaxed = true) {
                every { childCount } returns 1
                every { getChildAt(0) } returns nestedGroup
            }

        val result = parent.subViewsWithType(LinearLayout::class.java)
        assertEquals(listOf(deepChild), result)
    }

    @DisplayName("applyHorizontalCutoutPadding calls setPadding with cutout insets")
    @Test
    fun applyHorizontalCutoutPaddingCallsSetPadding() {
        val view = mockk<View>(relaxed = true)
        val insets = mockk<WindowInsetsCompat>(relaxed = true)

        view.applyHorizontalCutoutPadding(insets)

        verify { view.setPadding(any(), any(), any(), any()) }
    }

    @DisplayName("Given Judo with non-null metaData, toPaymentRequest includes metadata")
    @Test
    fun toPaymentRequestWithNonNullMetaData() {
        val actual = judoWithMetaData.toPaymentRequest("4111111111111111", "1229", "452", mockThreeDSecureTwo, "321321321")
        val json = Gson().toJson(actual)
        assertTrue(json.contains("key1"))
    }

    @DisplayName("Given Judo with non-null metaData, toSaveCardRequest includes metadata")
    @Test
    fun toSaveCardRequestWithNonNullMetaData() {
        val actual = judoWithMetaData.toSaveCardRequest("4111111111111111", "1229", "452")
        val json = Gson().toJson(actual)
        assertTrue(json.contains("key1"))
    }

    @DisplayName("Given Judo with non-null metaData, toCheckCardRequest includes metadata")
    @Test
    fun toCheckCardRequestWithNonNullMetaData() {
        val actual = judoWithMetaData.toCheckCardRequest("4111111111111111", "1229", "452", mockThreeDSecureTwo)
        val json = Gson().toJson(actual)
        assertTrue(json.contains("key1"))
    }

    @DisplayName("Given Judo with non-null metaData, toGooglePayRequest includes metadata")
    @Test
    fun toGooglePayRequestWithNonNullMetaData() {
        val actual = judoWithMetaData.toGooglePayRequest("VISA", "1234", "token123")
        val json = Gson().toJson(actual)
        assertTrue(json.contains("key1"))
    }

    @DisplayName("Given Judo with non-null metaData, toTokenRequest includes metadata")
    @Test
    fun toTokenRequestWithNonNullMetaData() {
        val actual = judoWithMetaData.toTokenRequest("cardToken", mockThreeDSecureTwo, "452")
        val json = Gson().toJson(actual)
        assertTrue(json.contains("key1"))
    }

    @DisplayName("Given Judo with null address, toPaymentRequest does not throw")
    @Test
    fun toPaymentRequestWithNullAddress() {
        val actual = judoNoAddress.toPaymentRequest("4111111111111111", "1229", "452", mockThreeDSecureTwo, "321321321")
        assertTrue(Gson().toJson(actual).isNotEmpty())
    }

    @DisplayName("Given Judo with null address, toSaveCardRequest does not throw")
    @Test
    fun toSaveCardRequestWithNullAddress() {
        val actual = judoNoAddress.toSaveCardRequest("4111111111111111", "1229", "452")
        assertTrue(Gson().toJson(actual).isNotEmpty())
    }

    @DisplayName("Given Judo with null address, toCheckCardRequest does not throw")
    @Test
    fun toCheckCardRequestWithNullAddress() {
        val actual = judoNoAddress.toCheckCardRequest("4111111111111111", "1229", "452", mockThreeDSecureTwo)
        assertTrue(Gson().toJson(actual).isNotEmpty())
    }

    @DisplayName("Given Judo with null address, toTokenRequest does not throw")
    @Test
    fun toTokenRequestWithNullAddress() {
        val actual = judoNoAddress.toTokenRequest("cardToken", mockThreeDSecureTwo, "452")
        assertTrue(Gson().toJson(actual).isNotEmpty())
    }

    @DisplayName("animateWithAlpha calls animate().alpha() with the given value")
    @Test
    fun animateWithAlphaDelegatesToAnimator() {
        val animator = mockk<ViewPropertyAnimator>(relaxed = true)
        every { animator.alpha(any()) } returns animator
        val view = mockk<View>(relaxed = true)
        every { view.animate() } returns animator

        view.animateWithAlpha(0.5f)

        verify { animator.alpha(0.5f) }
    }

    @DisplayName("animateWithAlpha with explicit duration sets duration on animator")
    @Test
    fun animateWithAlphaWithExplicitDuration() {
        val animator = mockk<ViewPropertyAnimator>(relaxed = true)
        every { animator.alpha(any()) } returns animator
        val view = mockk<View>(relaxed = true)
        every { view.animate() } returns animator

        view.animateWithAlpha(1.0f, 300L)

        verify { animator.alpha(1.0f) }
        verify { animator.setDuration(300L) }
    }

    @DisplayName("animateWithTranslation calls animate().translationY().alpha()")
    @Test
    fun animateWithTranslationDelegatesToAnimator() {
        val animator = mockk<ViewPropertyAnimator>(relaxed = true)
        every { animator.translationY(any()) } returns animator
        every { animator.alpha(any()) } returns animator
        val view = mockk<View>(relaxed = true)
        every { view.animate() } returns animator

        view.animateWithTranslation(100f, 0.8f)

        verify { animator.translationY(100f) }
        verify { animator.alpha(0.8f) }
    }

    @DisplayName("dismissKeyboard hides soft input when InputMethodManager is available")
    @Test
    fun dismissKeyboardHidesSoftInput() {
        val imm = mockk<InputMethodManager>(relaxed = true)
        val view = mockk<View>(relaxed = true)
        every { view.context.getSystemService(Context.INPUT_METHOD_SERVICE) } returns imm

        view.dismissKeyboard()

        verify { imm.hideSoftInputFromWindow(any(), 0) }
    }

    @DisplayName("dismissKeyboard does nothing when InputMethodManager is null")
    @Test
    fun dismissKeyboardDoesNothingWhenImmNull() {
        val view = mockk<View>(relaxed = true)
        every { view.context.getSystemService(Context.INPUT_METHOD_SERVICE) } returns null

        view.dismissKeyboard()
    }

    @DisplayName("setMaxTextSize calls setTextSize when textSize exceeds the computed max")
    @Test
    fun setMaxTextSizeCallsSetTextSizeWhenExceedsMax() {
        val metrics = DisplayMetrics().apply { density = 2.0f }
        val textView = mockk<TextView>(relaxed = true)
        every { textView.resources.displayMetrics } returns metrics
        every { textView.textSize } returns 200f

        textView.setMaxTextSize(50f)

        verify { textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 100f) }
    }

    @DisplayName("setMaxTextSize does not call setTextSize when textSize is within limit")
    @Test
    fun setMaxTextSizeDoesNotCallSetTextSizeWhenWithinLimit() {
        val metrics = DisplayMetrics().apply { density = 2.0f }
        val textView = mockk<TextView>(relaxed = true)
        every { textView.resources.displayMetrics } returns metrics
        every { textView.textSize } returns 50f

        textView.setMaxTextSize(50f)

        verify(exactly = 0) { textView.setTextSize(any(), any()) }
    }

    @DisplayName("moveCursorToEnd posts a runnable that calls setSelection at text length")
    @Test
    fun moveCursorToEndPostsSetSelectionRunnable() {
        val runnableSlot = slot<Runnable>()
        val editable = mockk<android.text.Editable> { every { length } returns 7 }
        val editText = mockk<EditText>(relaxed = true)
        every { editText.text } returns editable
        every { editText.post(capture(runnableSlot)) } returns true

        editText.moveCursorToEnd()
        runnableSlot.captured.run()

        verify { editText.setSelection(7) }
    }

    @DisplayName("applyDialogStyling configures the Window correctly")
    @Test
    fun applyDialogStylingConfiguresWindow() {
        val window = mockk<Window>(relaxed = true)
        every { window.requestFeature(any()) } returns true

        window.applyDialogStyling()

        verify { window.requestFeature(Window.FEATURE_NO_TITLE) }
        verify { window.setDimAmount(0.5f) }
    }
}
