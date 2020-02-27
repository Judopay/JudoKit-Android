package com.judopay.ui.cardentry.model

import androidx.annotation.StringRes
import com.judopay.R

interface FieldConfiguration {
    val type: FormFieldType
}

data class InputFieldConfiguration(override val type: FormFieldType,
                                   @StringRes val hint: Int = R.string.empty) : FieldConfiguration

data class SubmitFieldConfiguration(override val type: FormFieldType = FormFieldType.SUBMIT,
                                    val text: String) : FieldConfiguration

