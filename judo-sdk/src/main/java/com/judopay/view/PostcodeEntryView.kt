package com.judopay.view

import android.content.Context
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.judopay.R
import com.judopay.model.Country
import kotlinx.android.synthetic.main.view_postcode_entry.view.postCodeContainer
import kotlinx.android.synthetic.main.view_postcode_entry.view.postCodeEditText
import kotlinx.android.synthetic.main.view_postcode_entry.view.postCodeError

class PostcodeEntryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_postcode_entry, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        postCodeEditText?.onFocusChangeListener = HintFocusListener(
            postCodeEditText,
            resources.getString(R.string.billing_postcode)
        )
    }

    fun addTextChangedListener(watcher: TextWatcher?) {
        postCodeEditText.addTextChangedListener(watcher)
    }

    private fun setHint(@StringRes hint: Int) {
        postCodeEditText.hint = resources.getString(hint)
    }

    fun setError(@StringRes errorMessage: Int, show: Boolean) {
        val set = ConstraintSet()
        set.clone(postCodeContainer)
        if (show) {
            set.apply {
                clear(postCodeEditText.id, ConstraintSet.TOP)
                setMargin(postCodeEditText.id, ConstraintSet.BOTTOM, 6)
            }
            postCodeError.apply {
                visibility = View.VISIBLE
                setText(errorMessage)
            }
            postCodeEditText.setTextColor(ContextCompat.getColor(context, R.color.error))
        } else {
            set.apply {
                connect(
                    postCodeEditText.id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                setMargin(postCodeEditText.id, ConstraintSet.BOTTOM, 0)
            }
            postCodeEditText.setTextColor(ContextCompat.getColor(context, R.color.black))
            postCodeError.apply {
                visibility = View.GONE
                text = ""
            }
        }
        set.applyTo(postCodeContainer)
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

    fun getText(): String = postCodeEditText.text.toString().trim { it <= ' ' }

    fun getEditText(): JudoEditText = postCodeEditText

    fun setCountry(country: Country) {
        setHint(country.postcodeNameResourceId)
        val postcodeNumeric = Country.UNITED_STATES == country
        setNumericInput(postcodeNumeric)
        isEnabled = Country.OTHER != country
    }
}