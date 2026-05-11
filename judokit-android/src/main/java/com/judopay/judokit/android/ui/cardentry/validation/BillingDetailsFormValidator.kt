package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.AVSCountry
import com.judopay.judokit.android.model.american
import com.judopay.judokit.android.model.canadian
import com.judopay.judokit.android.model.chinese
import com.judopay.judokit.android.model.indian
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.AddressLineValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.CityValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.EmailValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.MobileNumberValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.PhoneCountryCodeValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.AdministrativeDivisionValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CountryValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.PostcodeValidator
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CANADA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CHINA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_INDIA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_US

private val COUNTRIES_REQUIRING_ADMIN_DIVISION = setOf(ALPHA_2_CODE_CANADA, ALPHA_2_CODE_CHINA, ALPHA_2_CODE_INDIA, ALPHA_2_CODE_US)

class BillingDetailsFormValidator {
    private val postcodeValidator = PostcodeValidator()
    private val adminDivisionValidator = AdministrativeDivisionValidator()

    private val validators: List<Validator> =
        listOf(
            EmailValidator(),
            CountryValidator(),
            adminDivisionValidator,
            PhoneCountryCodeValidator(),
            MobileNumberValidator(),
            CityValidator(),
            postcodeValidator,
            AddressLineValidator(),
        )

    var country: Country? = null
        set(value) {
            field = value
            postcodeValidator.country = AVSCountry.entries.firstOrNull { it.name == value?.alpha2Code } ?: AVSCountry.OTHER
            adminDivisionValidator.country = value
        }

    val adminDivisionRequired: Boolean
        get() = country?.alpha2Code in COUNTRIES_REQUIRING_ADMIN_DIVISION

    @Suppress("ReturnCount")
    fun validateField(
        fieldType: BillingDetailsFieldType,
        value: String,
        event: FormFieldEvent,
        phoneCountryCode: String,
        mobileNumber: String,
    ): ValidationResult? {
        if (fieldType == BillingDetailsFieldType.PHONE_COUNTRY_CODE ||
            fieldType == BillingDetailsFieldType.MOBILE_NUMBER
        ) {
            return validatePhoneFields(fieldType, value, event, phoneCountryCode, mobileNumber)
        }

        if (fieldType == BillingDetailsFieldType.ADMINISTRATIVE_DIVISION && !adminDivisionRequired) {
            return ValidationResult(true, R.string.jp_empty)
        }

        return validators.firstOrNull { it.fieldType == fieldType.name }?.validate(value, event)
    }

    fun isoCodeForAdminDivision(name: String): String? {
        val divisions =
            when (country?.alpha2Code) {
                ALPHA_2_CODE_CANADA -> canadian
                ALPHA_2_CODE_CHINA -> chinese
                ALPHA_2_CODE_INDIA -> indian
                ALPHA_2_CODE_US -> american
                else -> return null
            }
        return divisions.firstOrNull { it.name.equals(name, ignoreCase = true) }?.isoCode
    }

    private fun validatePhoneFields(
        fieldType: BillingDetailsFieldType,
        value: String,
        event: FormFieldEvent,
        phoneCountryCode: String,
        mobileNumber: String,
    ): ValidationResult? =
        when {
            phoneCountryCode.isEmpty() && mobileNumber.isEmpty() -> null
            phoneCountryCode.isEmpty() ->
                ValidationResult(
                    false,
                    if (event == FormFieldEvent.FOCUS_CHANGED) R.string.jp_invalid_phone_country_code else R.string.jp_empty,
                )
            else -> validators.firstOrNull { it.fieldType == fieldType.name }?.validate(value, event)
        }
}
