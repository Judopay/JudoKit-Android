package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.canadaProvincesAndTerritories
import com.judopay.judokit.android.model.chinaProvinces
import com.judopay.judokit.android.model.indiaStates
import com.judopay.judokit.android.model.usStates
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CANADA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CHINA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_INDIA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_US

data class StateValidator(
    var country: Country? = null,
    override val fieldType: String = BillingDetailsFieldType.STATE.name,
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
        val validStates =
            when (country?.alpha2Code) {
                ALPHA_2_CODE_CANADA -> canadaProvincesAndTerritories
                ALPHA_2_CODE_CHINA -> chinaProvinces
                ALPHA_2_CODE_INDIA -> indiaStates
                else -> usStates
            }
        val isValid = validStates.map { it.name.lowercase() }.contains(input.lowercase())
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
