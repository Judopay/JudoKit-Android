package com.judopay.ui.cardentry.components

import android.text.Editable
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.judopay.model.CardNetwork
import com.judopay.model.CardNetwork.Companion.DEFAULT_CARD_NUMBER_MASK
import com.judopay.model.cardNumberMask
import com.judopay.model.iconImageResId

internal class CardNumberInputMaskTextWatcher(private val editText: EditText) : InputMaskTextWatcher(editText, DEFAULT_CARD_NUMBER_MASK) {

    override fun afterTextChanged(s: Editable?) {
        // Adjust the mask according to detected card network if any
        val network = CardNetwork.ofCardNumber(s.toString())
        mask = network?.cardNumberMask ?: DEFAULT_CARD_NUMBER_MASK
        setCardNetworkLogo(network)

        // trigger parent formatting logic
        super.afterTextChanged(s)
    }

    private fun setCardNetworkLogo(network: CardNetwork?) = with(editText) {
        val resId = network?.iconImageResId ?: 0
        val drawable = if (resId > 0) ContextCompat.getDrawable(context, resId) else null
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }

}