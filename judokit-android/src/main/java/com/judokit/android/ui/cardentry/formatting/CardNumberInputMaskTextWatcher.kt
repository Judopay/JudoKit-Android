package com.judokit.android.ui.cardentry.formatting

import android.text.Editable
import android.widget.EditText
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.cardNumberMask
import com.judokit.android.model.iconImageResId
import com.judokit.android.parentOfType
import com.judokit.android.ui.cardentry.components.JudoEditTextInputLayout

internal class CardNumberInputMaskTextWatcher(
    private val editText: EditText,
    private val securityCodeMask: SecurityCodeInputMaskTextWatcher?,
    private var cardNetwork: CardNetwork? = null
) : InputMaskTextWatcher(editText, CardNetwork.OTHER.cardNumberMask) {

    override fun afterTextChanged(s: Editable?) {
        // adjust the mask according to detected card network if any
        val network = cardNetwork ?: CardNetwork.ofNumber(s.toString())

        mask = network.cardNumberMask

        // update card network logo icon
        editText.parentOfType(JudoEditTextInputLayout::class.java)?.let {
            it.accessoryImage = network.iconImageResId
        }

        // update security code mask
        securityCodeMask?.cardNetwork = network

        // trigger parent formatting logic
        super.afterTextChanged(s)
    }
}
