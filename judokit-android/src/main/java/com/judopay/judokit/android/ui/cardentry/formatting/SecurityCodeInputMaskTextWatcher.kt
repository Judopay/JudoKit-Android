package com.judopay.judokit.android.ui.cardentry.formatting

import android.widget.EditText
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.securityCodeNameOfCardNetwork
import com.judopay.judokit.android.model.securityCodeNumberMask
import com.judopay.judokit.android.model.securityCodeNumberMaskOfCardNetwork

internal class SecurityCodeInputMaskTextWatcher(
    private val editText: EditText,
) : InputMaskTextWatcher(editText, CardNetwork.OTHER.securityCodeNumberMask) {
    var cardNetwork: CardNetwork? = null
        set(value) {
            field = value
            hint = field.securityCodeNameOfCardNetwork
            mask = field.securityCodeNumberMaskOfCardNetwork
        }

    var hint: String = ""
        set(value) {
            field = value
            editText.hint = value
        }
}
