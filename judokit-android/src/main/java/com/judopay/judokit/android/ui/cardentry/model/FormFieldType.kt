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

val FormFieldType.fieldHintResId: Int
    get() = when (this) {
        FormFieldType.NUMBER -> R.string.card_number_hint
        FormFieldType.HOLDER_NAME -> R.string.card_holder_hint
        FormFieldType.EXPIRATION_DATE -> R.string.date_hint
        FormFieldType.SECURITY_NUMBER -> R.string.cvv_hint
        FormFieldType.COUNTRY -> R.string.country_hint
        FormFieldType.POST_CODE -> R.string.post_code_hint
    }
