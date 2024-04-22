package com.judopay.judokit.android.ui.cardentry.validation

import androidx.annotation.StringRes
import com.judopay.judokit.android.R

data class ValidationResult(
    val isValid: Boolean,
    @StringRes val message: Int = R.string.empty,
) {
    override fun toString(): String {
        return "ValidationResult(isValid=$isValid, message=$message)"
    }
}
