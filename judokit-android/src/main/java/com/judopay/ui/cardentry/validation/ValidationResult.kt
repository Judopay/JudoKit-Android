package com.judopay.ui.cardentry.validation

import androidx.annotation.StringRes
import com.judopay.R

data class ValidationResult(
    val isValid: Boolean,
    @StringRes val message: Int = R.string.empty
) {
    override fun toString(): String {
        return "ValidationResult(isValid=$isValid, message=$message)"
    }
}
