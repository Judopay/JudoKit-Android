package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context

internal fun <T> renderFieldErrors(
    errors: Map<T, Int?>,
    context: Context,
    layoutForType: (T) -> JudoEditTextInputLayout?,
) {
    for ((type, errorRes) in errors) {
        layoutForType(type)?.let { layout ->
            layout.isErrorEnabled = errorRes != null
            layout.error = if (errorRes != null) context.getString(errorRes) else ""
        }
    }
}
