package com.judopay.judokit.android.ui.cardentry.model

import com.judopay.judokit.android.R

enum class FormFieldType {
    NUMBER,
    HOLDER_NAME,
    EXPIRATION_DATE,
    SECURITY_NUMBER,
    COUNTRY,
    POST_CODE
}

enum class BillingDetailsFieldType {
    EMAIL,
    COUNTRY,
    PHONE_COUNTRY_CODE,
    MOBILE_NUMBER,
    ADDRESS_LINE_1,
    ADDRESS_LINE_2,
    ADDRESS_LINE_3,
    CITY,
    POST_CODE
}

val FormFieldType.fieldHintResId: Int
    get() = when (this) {
        FormFieldType.NUMBER -> R.string.card_number_hint
        FormFieldType.HOLDER_NAME -> R.string.card_holder_hint
        FormFieldType.EXPIRATION_DATE -> R.string.date_hint
        FormFieldType.SECURITY_NUMBER -> R.string.cvv_hint
        FormFieldType.COUNTRY -> R.string.country_hint
        FormFieldType.POST_CODE -> R.string.post_code_hint
    }

val BillingDetailsFieldType.fieldHintResId: Int
    get() = when (this) {
        BillingDetailsFieldType.COUNTRY -> R.string.country_hint
        BillingDetailsFieldType.POST_CODE -> R.string.post_code_hint
        BillingDetailsFieldType.EMAIL -> R.string.email_hint
        BillingDetailsFieldType.PHONE_COUNTRY_CODE -> R.string.phone_country_code_hint
        BillingDetailsFieldType.MOBILE_NUMBER -> R.string.mobile_number_hint
        BillingDetailsFieldType.ADDRESS_LINE_1 -> R.string.address_line_1_hint
        BillingDetailsFieldType.ADDRESS_LINE_2 -> R.string.address_line_2_hint
        BillingDetailsFieldType.ADDRESS_LINE_3 -> R.string.address_line_3_hint
        BillingDetailsFieldType.CITY -> R.string.city_hint
    }
