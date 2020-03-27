package com.judopay.ui.cardentry.components

import android.text.Editable
import android.widget.EditText
import com.judopay.model.CardNetwork
import com.judopay.model.CardNetwork.Companion.DEFAULT_CARD_NUMBER_MASK
import com.judopay.model.cardNumberMask
import com.judopay.model.iconImageResId
import com.judopay.model.securityCodeName
import com.judopay.model.securityCodeNumberMask
import com.judopay.parentOfType

internal class CardNumberInputMaskTextWatcher(
    private val editText: EditText,
    private val securityCodeMask: SecurityCodeInputMaskTextWatcher
) : InputMaskTextWatcher(editText, DEFAULT_CARD_NUMBER_MASK) {

    override fun afterTextChanged(s: Editable?) {
        // Adjust the mask according to detected card network if any
        val network = CardNetwork.ofNumber(s.toString())
        mask = network?.cardNumberMask ?: DEFAULT_CARD_NUMBER_MASK
        setCardNetworkLogo(network)

        // trigger parent formatting logic
        super.afterTextChanged(s)
    }

    private fun setCardNetworkLogo(network: CardNetwork?) = with(editText) {
        val resId = network?.iconImageResId ?: 0
        val layout = parentOfType(JudoEditTextInputLayout::class.java)
        layout?.accessoryImage = resId

        securityCodeMask.apply {
            hint = network?.securityCodeName ?: "CVV"
            mask = network?.securityCodeNumberMask ?: "###"
        }
    }
}