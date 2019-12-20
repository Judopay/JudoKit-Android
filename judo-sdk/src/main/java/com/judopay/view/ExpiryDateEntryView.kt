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
import kotlinx.android.synthetic.main.view_expiry_date_entry.view.expiryDateContainer
import kotlinx.android.synthetic.main.view_expiry_date_entry.view.expiryDateEditText
import kotlinx.android.synthetic.main.view_expiry_date_entry.view.expiryDateError

/**
 * A view that allows for a card expiry date to be entered or for a tokenized expiry number to be shown.
 */
class ExpiryDateEntryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_expiry_date_entry, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val dateFormat = resources.getString(R.string.date_format)
        val numberFormatTextWatcher =
            NumberFormatTextWatcher(expiryDateEditText, dateFormat)
        expiryDateEditText.addTextChangedListener(numberFormatTextWatcher)
    }

    fun setText(text: CharSequence?) {
        expiryDateEditText.setText(text)
    }

    fun addTextChangedListener(watcher: SimpleTextWatcher?) {
        expiryDateEditText.addTextChangedListener(watcher)
    }

    fun setExpiryDate(expiryDate: String) {
        expiryDateEditText.setText(expiryDate)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        expiryDateEditText.isEnabled = false
    }

    fun getText(): String = expiryDateEditText.text.toString().trim { it <= ' ' }

    fun getEditText(): JudoEditText = expiryDateEditText

    fun setValidation(validation: Validation) {
        val set = ConstraintSet()
        set.clone(expiryDateContainer)
        if (validation.isShowError) {
            set.apply {
                clear(expiryDateEditText.id, ConstraintSet.TOP)
                setMargin(expiryDateEditText.id, ConstraintSet.BOTTOM, 6)
            }
            expiryDateError.apply {
                visibility = View.VISIBLE
                setText(validation.error)
            }
            expiryDateEditText.setTextColor(ContextCompat.getColor(context, R.color.error))
        } else {
            set.apply {
                connect(
                    expiryDateEditText.id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                setMargin(expiryDateEditText.id, ConstraintSet.BOTTOM, 0)
            }
            expiryDateEditText.setTextColor(ContextCompat.getColor(context, R.color.black))
            expiryDateError.apply {
                visibility = View.GONE
                text = ""
            }
        }
        set.applyTo(expiryDateContainer)
    }
}