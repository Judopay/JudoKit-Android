package com.judopay.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import com.judopay.R
import com.judopay.model.Country
import kotlinx.android.synthetic.main.view_postcode_entry.view.postCodeContainer
import kotlinx.android.synthetic.main.view_postcode_entry.view.postCodeEditText
import kotlinx.android.synthetic.main.view_postcode_entry.view.postCodeError

class PostcodeEntryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CardEntryView(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_postcode_entry, this)
        container = postCodeContainer
        editText = postCodeEditText
        error = postCodeError
    }

    private fun setHint(@StringRes hint: Int) {
        postCodeEditText.hint = resources.getString(hint)
    }

    private fun setNumericInput(numeric: Boolean) {
        postCodeEditText?.let {
            if (numeric && it.inputType != InputType.TYPE_CLASS_NUMBER) {
                it.inputType = InputType.TYPE_CLASS_NUMBER
            } else {
                val alphanumericInputTypes =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                if (!numeric && it.inputType != alphanumericInputTypes) {
                    it.inputType = alphanumericInputTypes
                }
            }
            it.privateImeOptions = "nm" // prevent text suggestions in keyboard
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        postCodeEditText.isEnabled = enabled
    }

    fun setCountry(country: Country) {
        setHint(country.postcodeNameResourceId)
        val postcodeNumeric = Country.UNITED_STATES == country
        setNumericInput(postcodeNumeric)
        isEnabled = Country.OTHER != country
    }
}