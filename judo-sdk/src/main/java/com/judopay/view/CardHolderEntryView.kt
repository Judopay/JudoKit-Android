package com.judopay.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.judopay.R
import com.judopay.validation.Validation
import kotlinx.android.synthetic.main.view_card_holder_entry.view.cardHolderContainer
import kotlinx.android.synthetic.main.view_card_holder_entry.view.cardHolderEditText
import kotlinx.android.synthetic.main.view_card_holder_entry.view.cardHolderError

class CardHolderEntryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_card_holder_entry, this)
    }

    fun setText(text: String?) {
        cardHolderEditText.setText(text)
    }

    fun getText(): String = cardHolderEditText.text.toString()

    fun addTextChangedListener(watcher: SimpleTextWatcher?) {
        cardHolderEditText.addTextChangedListener(watcher)
    }

    fun setValidation(validation: Validation) {
        val set = ConstraintSet()
        set.clone(cardHolderContainer)
        if (validation.isShowError) {
            set.apply {
                clear(cardHolderEditText.id, ConstraintSet.TOP)
                setMargin(cardHolderEditText.id, ConstraintSet.BOTTOM, 6)
            }
            cardHolderError.apply {
                visibility = View.VISIBLE
                setText(validation.error)
            }
            cardHolderEditText.setTextColor(ContextCompat.getColor(context, R.color.error))
        } else {
            set.apply {
                connect(
                    cardHolderEditText.id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                set.setMargin(cardHolderEditText.id, ConstraintSet.BOTTOM, 0)
            }
            cardHolderEditText.setTextColor(ContextCompat.getColor(context, R.color.black))
            cardHolderError.apply {
                visibility = View.GONE
                text = ""
            }
        }
        set.applyTo(cardHolderContainer)
    }

    fun getEditText(): JudoEditText = cardHolderEditText
}