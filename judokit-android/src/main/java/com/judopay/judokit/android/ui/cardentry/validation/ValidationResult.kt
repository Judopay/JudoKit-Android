package com.judopay.judokit.android.ui.cardentry.validation

import androidx.annotation.StringRes
import com.judopay.judokit.android.R

data class ValidationResult(
    val isValid: Boolean,
    @StringRes val message: Int = R.string.jp_empty,
) {
    override fun toString(): String = "ValidationResult(isValid=$isValid, message=$message)"
}
