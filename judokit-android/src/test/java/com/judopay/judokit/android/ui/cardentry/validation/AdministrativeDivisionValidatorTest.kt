package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.AdministrativeDivisionValidator
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CANADA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CHINA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_GB
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_INDIA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_US
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class AdministrativeDivisionValidatorTest {
    private val countryUK = Country(ALPHA_2_CODE_GB, "United Kingdom", "44", "826", "#### ######")
    private val countryUS = Country(ALPHA_2_CODE_US, "United States", "1", "840", "(###) ###-####")
    private val countryCanada = Country(ALPHA_2_CODE_CANADA, "Canada", "1", "124", "(###) ###-####")
    private val countryChina = Country(ALPHA_2_CODE_CHINA, "China", "86", "156", "##-#########")
    private val countryIndia = Country(ALPHA_2_CODE_INDIA, "India", "91", "356", "#####-#####")

    private val validator: AdministrativeDivisionValidator = AdministrativeDivisionValidator()

    @Test
    @DisplayName(
        "Given that no matching administrative division is selected for the US, then a validation " +
            "error should be returned with the correct message",
    )
    fun invalidWhenInputIsBlankForUS() {
        validator.country = countryUS
        assertEquals(
            validator.validate("Alaba", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_error_state_should_not_be_empty),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given that no matching administrative division is selected for Canada, then validation error should be returned with the correct message",
    )
    fun invalidWhenInputIsBlankForCA() {
        validator.country = countryCanada
        assertEquals(
            validator.validate("Alb", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_error_province_territory_should_not_be_empty),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given that no matching administrative division is selected for China, then validation error should be returned with the correct message",
    )
    fun invalidWhenInputIsBlankForCN() {
        validator.country = countryChina
        assertEquals(
            validator.validate("Alb", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_error_province_region_should_not_be_empty),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given that no matching administrative division is selected for India, then validation error should be returned with the correct message",
    )
    fun invalidWhenInputIsBlankForIN() {
        validator.country = countryIndia
        assertEquals(
            validator.validate("Alb", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_error_state_union_territory_should_not_be_empty),
        )
    }

    @Test
    @DisplayName("Given that the user is typing the administrative division name, then there should be no validation error message")
    fun validWhenInputIsBeingTypedForCA() {
        validator.country = countryCanada
        assertEquals(
            validator.validate("Alb", FormFieldEvent.TEXT_CHANGED),
            ValidationResult(false, R.string.jp_empty),
        )
    }

    @Test
    @DisplayName("Given that an administrative division is selected for Canada, then validation passes")
    fun validWhenInputIsValidForCA() {
        validator.country = countryCanada
        assertEquals(validator.validate("Alberta", FormFieldEvent.FOCUS_CHANGED), ValidationResult(true))
    }

    @Test
    @DisplayName("Given that an administrative division is selected for the US, then validation passes")
    fun validWhenInputIsValidForUS() {
        validator.country = countryUS
        assertEquals(validator.validate("Alabama", FormFieldEvent.FOCUS_CHANGED), ValidationResult(true))
    }

    @Test
    @DisplayName("Given that an administrative division is selected for China, then validation passes")
    fun validWhenInputIsValidForCN() {
        validator.country = countryChina
        assertEquals(validator.validate("Anhui Sheng", FormFieldEvent.FOCUS_CHANGED), ValidationResult(true))
    }

    @Test
    @DisplayName("Given that an administrative division is selected for India, then validation passes")
    fun validWhenInputIsValidForIN() {
        validator.country = countryIndia
        assertEquals(validator.validate("Andaman and Nicobar Islands", FormFieldEvent.FOCUS_CHANGED), ValidationResult(true))
    }

    @Test
    @DisplayName(
        "Given that an administrative division is selected for a country that has no administrative divisions, then validation passes",
    )
    fun validWhenInputIsNotBlankForGB() {
        validator.country = countryUK
        assertEquals(validator.validate("Whatever state", FormFieldEvent.FOCUS_CHANGED), ValidationResult(true))
    }

    @Test
    @DisplayName(
        "Given that an administrative division is not selected for a country that has no administrative divisions, then validation passes",
    )
    fun validWhenInputIsBlankForOtherCountry() {
        validator.country = countryUK
        assertEquals(validator.validate("", FormFieldEvent.FOCUS_CHANGED), ValidationResult(true))
    }
}
