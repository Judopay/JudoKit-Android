package com.judopay.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.judopay.R
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
) : CardEntryView(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_expiry_date_entry, this)
        container = expiryDateContainer
        editText = expiryDateEditText
        error = expiryDateError
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val dateFormat = resources.getString(R.string.date_format)
        val numberFormatTextWatcher =
            NumberFormatTextWatcher(expiryDateEditText, dateFormat)
        expiryDateEditText.addTextChangedListener(numberFormatTextWatcher)
    }

    fun setExpiryDate(expiryDate: String) {
        expiryDateEditText.setText(expiryDate)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        expiryDateEditText.isEnabled = false
    }
}