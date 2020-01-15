package com.judopay.view

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import com.judopay.R
import com.judopay.model.CardNetwork
import com.judopay.model.CardToken
import kotlinx.android.synthetic.main.view_card_number_entry.view.cardNumberContainer
import kotlinx.android.synthetic.main.view_card_number_entry.view.cardNumberEditText
import kotlinx.android.synthetic.main.view_card_number_entry.view.cardNumberError
import kotlinx.android.synthetic.main.view_card_number_entry.view.cardNumberImageView


/**
 * A view that allows for card number data to be input by the user and the detected card type
 * to be displayed alongside the card number.
 * Does not perform validation itself, this is done by the {@link com.judopay.validation.CardNumberValidator}
 * class.
 */
class CardNumberEntryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CardEntryView(context, attrs, defStyle) {

    private lateinit var numberFormatTextWatcher: NumberFormatTextWatcher
    private var cardType = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_card_number_entry, this)
        container = cardNumberContainer
        editText = cardNumberEditText
        error = cardNumberError
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        numberFormatTextWatcher =
            NumberFormatTextWatcher(
                cardNumberEditText,
                resources.getString(R.string.card_number_format)
            )
        cardNumberEditText.addTextChangedListener(numberFormatTextWatcher)
    }

    override fun onSaveInstanceState(): Parcelable? = Bundle().apply {
        putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState())
        putInt(KEY_CARD_TYPE, cardType)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val superState = state.getParcelable<Parcelable>(KEY_SUPER_STATE)
            setCardType(state.getInt(KEY_CARD_TYPE), false)
            super.onRestoreInstanceState(superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun setCardType(type: Int, animated: Boolean) {
        cardType = type
        val imageResId = getImageResource(type)
        cardNumberImageView?.apply {
            if (tag != imageResId) {
                toggleImageVisibility(imageResId, animated)
            }
            tag = imageResId
        }
        when (type) {
            CardNetwork.AMEX -> {
                setMaxLength(17)
                numberFormatTextWatcher.setFormat(resources.getString(R.string.amex_card_number_format))
            }
            CardNetwork.DINERS_CLUB_INTERNATIONAL -> {
                setMaxLength(16)
                numberFormatTextWatcher.setFormat(resources.getString(R.string.diners_club_international_card_number_format))
            }
            else -> {
                setMaxLength(19)
                numberFormatTextWatcher.setFormat(resources.getString(R.string.card_number_format))

            }
        }
    }

    private fun setMaxLength(maxLength: Int) {
        cardNumberEditText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
    }

    fun getCardType(): Int = CardNetwork.fromCardNumber(cardNumberEditText.text.toString())

    override fun getText(): String? = cardNumberEditText.text.toString().replace(" ".toRegex(), "")

    fun setTokenCard(cardToken: CardToken) {
        val amex = cardToken.type == CardNetwork.AMEX
        cardNumberEditText.apply {
            isEnabled = false
            removeTextChangedListener(numberFormatTextWatcher)
            setText(
                context.getString(
                    if (amex) R.string.amex_token_card_number else R.string.token_card_number,
                    cardToken.lastFour
                )
            )
            addTextChangedListener(numberFormatTextWatcher)
        }
    }

    private fun toggleImageVisibility(@DrawableRes imageResId: Int, animated: Boolean) {
        cardNumberImageView?.let {
            val hasImage = imageResId != 0
            val to = if (hasImage) 1f else 0f
            val from = if (hasImage) 0f else 1f

            it.alpha = if (animated) from else to

            ObjectAnimator.ofFloat(it, View.ALPHA, from, to).apply { duration = 300 }.start()

            if (imageResId != 0) {
                it.setImageResource(imageResId)
            }
        }
    }

    private fun getImageResource(type: Int): Int {
        return when (type) {
            CardNetwork.AMEX -> R.drawable.ic_card_amex
            CardNetwork.MASTERCARD -> R.drawable.ic_card_mastercard
            CardNetwork.MAESTRO -> R.drawable.ic_card_maestro
            CardNetwork.VISA, CardNetwork.VISA_ELECTRON, CardNetwork.VISA_DEBIT -> R.drawable.ic_card_visa
            CardNetwork.DISCOVER -> R.drawable.ic_discover
            CardNetwork.DINERS_CLUB_INTERNATIONAL -> R.drawable.ic_diners
            CardNetwork.JCB -> R.drawable.ic_jcb
            else -> 0
        }
    }
}