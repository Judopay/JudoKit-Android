package com.judokit.android.ui.cardentry.formatting

import android.text.Editable
import android.widget.EditText
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.CardNetwork.Companion.DEFAULT_CARD_NUMBER_MASK
import com.judokit.android.model.cardNumberMask
import com.judokit.android.model.iconImageResId
import com.judokit.android.model.securityCodeNameOfCardNetwork
import com.judokit.android.model.securityCodeNumberMaskOfCardNetwork
import com.judokit.android.parentOfType
import com.judokit.android.ui.cardentry.components.JudoEditTextInputLayout

internal class CardNumberInputMaskTextWatcher(
    private val editText: EditText,
    private val securityCodeMask: SecurityCodeInputMaskTextWatcher?,
    private val selectedCardNetwork: CardNetwork? = null
) : InputMaskTextWatcher(editText, DEFAULT_CARD_NUMBER_MASK) {

    override fun afterTextChanged(s: Editable?) {
        // Adjust the mask according to detected card network if any
        val network = CardNetwork.ofNumber(s.toString())
        mask = network.cardNumberMask
        setCardNetworkLogo(network)

        // trigger parent formatting logic
        super.afterTextChanged(s)
    }

    private fun setCardNetworkLogo(network: CardNetwork) = with(editText) {
        val resId = network.iconImageResId
        val layout = parentOfType(JudoEditTextInputLayout::class.java)
        layout?.accessoryImage = resId

        val myNetwork = selectedCardNetwork ?: network
        securityCodeMask.apply {
            this?.hint = myNetwork.securityCodeNameOfCardNetwork
            this?.mask = myNetwork.securityCodeNumberMaskOfCardNetwork
        }
    }
}
