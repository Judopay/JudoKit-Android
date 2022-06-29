package com.judopay.judokit.android.ui.cardentry.formatting

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher

internal class PhoneCountryCodeTextWatcher : TextWatcher {
    private var isSelfFormatting = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        if (isSelfFormatting) return

        isSelfFormatting = true

        if (s.isNullOrEmpty()) return

        val formatted = "+(${s.filter { it.isDigit() }})"
        s.replace(0, s.length, formatted, 0, formatted.length)
        Selection.setSelection(s, s.length - 1)

        isSelfFormatting = false
    }
}
