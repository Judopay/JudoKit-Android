package com.judopay.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import com.judopay.R
import com.judopay.model.CardNetwork
import kotlinx.android.synthetic.main.view_security_code_entry.view.securityCodeContainer
import kotlinx.android.synthetic.main.view_security_code_entry.view.securityCodeEditText
import kotlinx.android.synthetic.main.view_security_code_entry.view.securityCodeError

const val KEY_SUPER_STATE = "superState"
const val KEY_CARD_TYPE = "cardType"

/**
 * A view that allows for the security code of a card (CV2, CID) to be input and an image displayed to
 * indicate where on the payment card the security code can be located.
 */
class SecurityCodeEntryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CardEntryView(context, attrs, defStyle) {

    private var hintFocusListener: HintFocusListener? = null
    private var cardType = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_security_code_entry, this)
        container = securityCodeContainer
        editText = securityCodeEditText
        error = securityCodeError
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        hintFocusListener = HintFocusListener(securityCodeEditText, "CVV")
        securityCodeEditText.onFocusChangeListener = MultiOnFocusChangeListener(hintFocusListener)
    }

    override fun onSaveInstanceState(): Parcelable? = Bundle().apply {
        putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState())
        putInt(KEY_CARD_TYPE, cardType)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val superState =
                state.getParcelable<Parcelable>(KEY_SUPER_STATE)
            setCardType(state.getInt(KEY_CARD_TYPE), false)
            super.onRestoreInstanceState(superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun setText(text: CharSequence?) {
        securityCodeEditText.setText(text)
    }

    fun setCardType(cardType: Int, animate: Boolean) {
        this.cardType = cardType
        setHint(CardNetwork.securityCode(cardType))
        setAlternateHint(CardNetwork.securityCode(cardType))
        setMaxLength(CardNetwork.securityCodeLength(cardType))
    }

    private fun setMaxLength(length: Int) {
        securityCodeEditText.filters = arrayOf<InputFilter>(LengthFilter(length))
    }

    private fun setHint(hint: String) {
        securityCodeEditText.hint = hint
    }

    private fun setAlternateHint(hint: String) {
        hintFocusListener?.setHint(hint)
    }
}