package com.judokit.android.ui.cardentry.formatting

import android.widget.EditText

internal class SecurityCodeInputMaskTextWatcher(
    private val editText: EditText
) : InputMaskTextWatcher(editText, "###") {

    var hint: String = ""
        set(value) {
            field = value
            editText.hint = value
        }
}
