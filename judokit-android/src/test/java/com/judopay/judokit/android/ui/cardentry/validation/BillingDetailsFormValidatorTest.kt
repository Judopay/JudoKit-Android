package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CANADA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CHINA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_INDIA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_US
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Test BillingDetailsFormValidator")
internal class BillingDetailsFormValidatorTest {
    private lateinit var sut: BillingDetailsFormValidator

    private fun makeCountry(alpha2Code: String) =
        Country(
            alpha2Code = alpha2Code,
            name = "Test",
            dialCode = "1",
            numericCode = "001",
            phoneNumberFormat = null,
        )

    @BeforeEach
    fun setUp() {
        sut = BillingDetailsFormValidator()
    }

    @Nested
    @DisplayName("adminDivisionRequired")
    inner class AdminDivisionRequiredTests {
        @Test
        @DisplayName("Given US country, then adminDivisionRequired is true")
        fun trueForUS() {
            sut.country = makeCountry(ALPHA_2_CODE_US)
            assertTrue(sut.adminDivisionRequired)
        }

        @Test
        @DisplayName("Given CA country, then adminDivisionRequired is true")
        fun trueForCA() {
            sut.country = makeCountry(ALPHA_2_CODE_CANADA)
            assertTrue(sut.adminDivisionRequired)
        }

        @Test
        @DisplayName("Given CN country, then adminDivisionRequired is true")
        fun trueForCN() {
            sut.country = makeCountry(ALPHA_2_CODE_CHINA)
            assertTrue(sut.adminDivisionRequired)
        }

        @Test
        @DisplayName("Given IN country, then adminDivisionRequired is true")
        fun trueForIN() {
            sut.country = makeCountry(ALPHA_2_CODE_INDIA)
            assertTrue(sut.adminDivisionRequired)
        }

        @Test
        @DisplayName("Given GB country, then adminDivisionRequired is false")
        fun falseForGB() {
            sut.country = makeCountry("GB")
            assertFalse(sut.adminDivisionRequired)
        }

        @Test
        @DisplayName("Given null country, then adminDivisionRequired is false")
        fun falseForNull() {
            sut.country = null
            assertFalse(sut.adminDivisionRequired)
        }
    }

    @Nested
    @DisplayName("validateField - phone fields")
    inner class PhoneFieldValidationTests {
        @Test
        @DisplayName("Given both phoneCountryCode and mobileNumber are empty, then returns null")
        fun returnsNullWhenBothPhoneFieldsEmpty() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.PHONE_COUNTRY_CODE,
                    "",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertNull(result)
        }

        @Test
        @DisplayName(
            "Given mobileNumber is not empty and phoneCountryCode is empty with FOCUS_CHANGED, then returns invalid with phone code error",
        )
        fun returnsPhoneCountryCodeErrorOnFocusChangedWhenCodeEmptyAndMobileSet() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.PHONE_COUNTRY_CODE,
                    "",
                    FormFieldEvent.FOCUS_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "07912345678",
                )
            assertEquals(ValidationResult(false, R.string.jp_invalid_phone_country_code), result)
        }

        @Test
        @DisplayName(
            "Given mobileNumber is not empty and phoneCountryCode is empty with TEXT_CHANGED, then returns invalid with empty message",
        )
        fun returnsEmptyMessageOnTextChangedWhenCodeEmptyAndMobileSet() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.PHONE_COUNTRY_CODE,
                    "",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "07912345678",
                )
            assertEquals(ValidationResult(false, R.string.jp_empty), result)
        }

        @Test
        @DisplayName("Given valid phoneCountryCode and mobileNumber, then delegates to phone country code validator")
        fun delegatesToPhoneCountryCodeValidatorWhenBothSet() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.PHONE_COUNTRY_CODE,
                    "44",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "44",
                    mobileNumber = "07912345678",
                )
            assertTrue(result != null)
        }

        @Test
        @DisplayName("Given MOBILE_NUMBER field when both phone fields are empty, then returns null")
        fun mobileNumberReturnsNullWhenBothPhoneFieldsEmpty() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.MOBILE_NUMBER,
                    "",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertNull(result)
        }

        @Test
        @DisplayName("Given MOBILE_NUMBER field with valid phone code set, then delegates to mobile number validator")
        fun mobileNumberDelegatesToValidatorWhenPhoneCodeSet() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.MOBILE_NUMBER,
                    "07912345678",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "44",
                    mobileNumber = "07912345678",
                )
            assertNotNull(result)
        }
    }

    @Nested
    @DisplayName("validateField - administrative division")
    inner class AdminDivisionValidationTests {
        @Test
        @DisplayName("Given admin division field and country not requiring it, then returns valid result")
        fun returnsValidWhenAdminDivisionNotRequired() {
            sut.country = makeCountry("GB")
            val result =
                sut.validateField(
                    BillingDetailsFieldType.ADMINISTRATIVE_DIVISION,
                    "",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertEquals(ValidationResult(true, R.string.jp_empty), result)
        }

        @Test
        @DisplayName("Given admin division field and US country, then delegates to admin division validator")
        fun delegatesToAdminDivisionValidatorWhenRequired() {
            sut.country = makeCountry(ALPHA_2_CODE_US)
            val result =
                sut.validateField(
                    BillingDetailsFieldType.ADMINISTRATIVE_DIVISION,
                    "California",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertTrue(result != null)
        }
    }

    @Nested
    @DisplayName("validateField - regular fields")
    inner class RegularFieldValidationTests {
        @Test
        @DisplayName("Given country field with non-empty value, then returns valid result")
        fun validatesCountryField() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.COUNTRY,
                    "United Kingdom",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertEquals(ValidationResult(true), result)
        }

        @Test
        @DisplayName("Given city field with valid city name, then returns valid result")
        fun validatesCityField() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.CITY,
                    "London",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertNotNull(result)
            assertTrue(result!!.isValid)
        }

        @Test
        @DisplayName("Given city field with empty value, then returns invalid result")
        fun invalidatesEmptyCityField() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.CITY,
                    "",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertNotNull(result)
            assertFalse(result!!.isValid)
        }

        @Test
        @DisplayName("Given post code field, then returns a non-null result")
        fun validatesPostCodeField() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.POST_CODE,
                    "SW1A 1AA",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertNotNull(result)
        }

        @Test
        @DisplayName("Given ADDRESS_LINE_1 field with a value, then returns a non-null result")
        fun validatesAddressLineOneField() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.ADDRESS_LINE_1,
                    "123 Main Street",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertNotNull(result)
        }

        @Test
        @DisplayName("Given ADDRESS_LINE_2 field, then returns null because no validator is registered for it")
        fun addressLineTwoReturnsNullWithNoMatchingValidator() {
            val result =
                sut.validateField(
                    BillingDetailsFieldType.ADDRESS_LINE_2,
                    "Apt 5",
                    FormFieldEvent.TEXT_CHANGED,
                    phoneCountryCode = "",
                    mobileNumber = "",
                )
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("isoCodeForAdminDivision")
    inner class IsoCodeForAdminDivisionTests {
        @Test
        @DisplayName("Given US country and state name Alaska, then returns AK")
        fun returnsIsoCodeForUSState() {
            sut.country = makeCountry(ALPHA_2_CODE_US)
            assertEquals("AK", sut.isoCodeForAdminDivision("Alaska"))
        }

        @Test
        @DisplayName("Given US country and state name with different case, then still returns isoCode")
        fun returnsIsoCodeForUSStateCaseInsensitive() {
            sut.country = makeCountry(ALPHA_2_CODE_US)
            assertEquals("AK", sut.isoCodeForAdminDivision("ALASKA"))
        }

        @Test
        @DisplayName("Given CA country and province name Ontario, then returns ON")
        fun returnsIsoCodeForCanadianProvince() {
            sut.country = makeCountry(ALPHA_2_CODE_CANADA)
            assertEquals("ON", sut.isoCodeForAdminDivision("Ontario"))
        }

        @Test
        @DisplayName("Given CN country and province name Beijing Shi, then returns BJ")
        fun returnsIsoCodeForChineseProvince() {
            sut.country = makeCountry(ALPHA_2_CODE_CHINA)
            assertEquals("BJ", sut.isoCodeForAdminDivision("Beijing Shi"))
        }

        @Test
        @DisplayName("Given IN country and state name Goa, then returns GA")
        fun returnsIsoCodeForIndianState() {
            sut.country = makeCountry(ALPHA_2_CODE_INDIA)
            assertEquals("GA", sut.isoCodeForAdminDivision("Goa"))
        }

        @Test
        @DisplayName("Given GB country, then returns null")
        fun returnsNullForNonSupportedCountry() {
            sut.country = makeCountry("GB")
            assertNull(sut.isoCodeForAdminDivision("London"))
        }

        @Test
        @DisplayName("Given null country, then returns null")
        fun returnsNullForNullCountry() {
            sut.country = null
            assertNull(sut.isoCodeForAdminDivision("Alaska"))
        }

        @Test
        @DisplayName("Given US country and unknown division name, then returns null")
        fun returnsNullForUnknownDivisionName() {
            sut.country = makeCountry(ALPHA_2_CODE_US)
            assertNull(sut.isoCodeForAdminDivision("Unknown Province"))
        }
    }
}
