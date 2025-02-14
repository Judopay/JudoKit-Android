package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.american
import com.judopay.judokit.android.model.canadian
import com.judopay.judokit.android.model.chinese
import com.judopay.judokit.android.model.indian
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CANADA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CHINA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_INDIA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_US

data class AdministrativeDivisionValidator(
    var country: Country? = null,
    override val fieldType: String = BillingDetailsFieldType.ADMINISTRATIVE_DIVISION.name,
) : Validator {
    override fun validate(
        input: String,
        formFieldEvent: FormFieldEvent,
    ): ValidationResult {
        if (
            country?.alpha2Code !in
            setOf(
                ALPHA_2_CODE_CANADA,
                ALPHA_2_CODE_CHINA,
                ALPHA_2_CODE_INDIA,
                ALPHA_2_CODE_US,
            )
        ) {
            return ValidationResult(true, R.string.jp_empty)
        }
        val validAdministrativeDivisions =
            when (country?.alpha2Code) {
                ALPHA_2_CODE_CANADA -> canadian
                ALPHA_2_CODE_CHINA -> chinese
                ALPHA_2_CODE_INDIA -> indian
                else -> american
            }
        val isValid = validAdministrativeDivisions.map { it.name.lowercase() }.contains(input.lowercase())
        val message =
            when {
                isValid || formFieldEvent == FormFieldEvent.TEXT_CHANGED -> R.string.jp_empty
                country?.alpha2Code == ALPHA_2_CODE_CANADA -> R.string.jp_error_province_territory_should_not_be_empty
                country?.alpha2Code == ALPHA_2_CODE_CHINA -> R.string.jp_error_province_region_should_not_be_empty
                country?.alpha2Code == ALPHA_2_CODE_INDIA -> R.string.jp_error_state_union_territory_should_not_be_empty
                else -> R.string.jp_error_state_should_not_be_empty
            }
        return ValidationResult(isValid, message)
    }
}
