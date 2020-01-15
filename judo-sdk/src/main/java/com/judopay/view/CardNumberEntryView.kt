package com.judopay.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.judopay.R
import com.judopay.model.CardNetwork
import com.judopay.model.CardToken
import com.judopay.validation.Validation
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
) : FrameLayout(context, attrs, defStyle) {

    private lateinit var numberFormatTextWatcher: NumberFormatTextWatcher
    private var cardType = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_card_number_entry, this)
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

    fun setCardType(type: Int, animate: Boolean) {
        cardType = type
        cardNumberImageView.setImageType(type, animate)
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

    fun setText(text: String?) {
        cardNumberEditText.setText(text)
    }

    private fun setMaxLength(maxLength: Int) {
        cardNumberEditText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
    }

    fun getCardType(): Int = CardNetwork.fromCardNumber(cardNumberEditText.text.toString())

    fun getText(): String? = cardNumberEditText.text.toString().replace(" ".toRegex(), "")

    fun addTextChangedListener(watcher: SimpleTextWatcher?) {
        cardNumberEditText.addTextChangedListener(watcher)
    }

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

    fun setValidation(validation: Validation) {
        val set = ConstraintSet()
        set.clone(cardNumberContainer)
        if (validation.isShowError) {
            set.apply {
                clear(cardNumberEditText.id, ConstraintSet.TOP)
                setMargin(cardNumberEditText.id, ConstraintSet.BOTTOM, 6)
            }
            cardNumberEditText.apply {
                visibility = View.VISIBLE
                setTextColor(ContextCompat.getColor(context, R.color.error))
            }
            cardNumberError.setText(validation.error)
        } else {
            set.apply {
                connect(
                    cardNumberEditText.id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                setMargin(cardNumberEditText.id, ConstraintSet.BOTTOM, 0)
            }
            cardNumberEditText.setTextColor(ContextCompat.getColor(context, R.color.black))
            cardNumberError.apply {
                visibility = View.GONE
                text = ""
            }
        }
        set.applyTo(cardNumberContainer)
    }

    fun getEditText(): JudoEditText = cardNumberEditText
}