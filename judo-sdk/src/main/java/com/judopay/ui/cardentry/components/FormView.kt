package com.judopay.ui.cardentry.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.judopay.R
import com.judopay.inflate
import com.judopay.model.CardNetwork
import com.judopay.parentOfType
import com.judopay.subViewsWithType
import com.judopay.ui.cardentry.components.FormFieldType.*
import com.judopay.ui.cardentry.model.FormModel
import com.judopay.ui.cardentry.validation.*
import kotlinx.android.synthetic.main.form_view.view.*

enum class FormFieldType {
    NUMBER,
    HOLDER_NAME,
    EXPIRATION_DATE,
    SECURITY_NUMBER,
    COUNTRY,
    POST_CODE
}

data class FormViewModel(
        val formModel: FormModel,
        val enabledFields: List<FormFieldType>,
        val supportedNetworks: List<CardNetwork>,
        @StringRes val submitButtonText: Int = R.string.button_add_card
)

class FormView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.form_view, true)
    }

    interface OnSubmitListener {
        fun onSubmitForm(form: FormView, model: FormModel)
    }

    var model = FormViewModel(FormModel(),
            listOf(NUMBER, EXPIRATION_DATE, SECURITY_NUMBER),
            listOf(CardNetwork.VISA))
        set(value) {
            field = value
            update()
        }

    internal var onSubmitListener: OnSubmitListener? = null
    private val validationResultsCache = mutableMapOf<FormFieldType, Boolean>()

    private val validators: List<Validator> = listOf(
            CardNumberValidator(supportedNetworks = model.supportedNetworks),
            CardHolderNameValidator(),
            ExpirationDateValidator(),
            SecurityCodeValidator()
    )

    override fun onFinishInflate() {
        super.onFinishInflate()

        with(editTextForType(EXPIRATION_DATE)) {
            val mask = InputMaskTextWatcher(this, "##/##")
            addTextChangedListener(mask)
        }

        val securityCode = editTextForType(SECURITY_NUMBER)
        val securityCodeMask = SecurityCodeInputMaskTextWatcher(securityCode)
        securityCode.addTextChangedListener(securityCodeMask)

        with(editTextForType(NUMBER)) {
            val mask = CardNumberInputMaskTextWatcher(this, securityCodeMask)
            addTextChangedListener(mask)
        }

        setupFields()
    }

    private fun setupFields() {
        submitButton.apply {
            setOnClickListener(::onSubmit)
            setText(model.submitButtonText)
            isEnabled = false
        }

        setupVisibilityOfFields()

        values().forEach { type ->
            editTextForType(type).apply {
                setHint(hintForFieldType(type))

                addTextChangedListener {
                    val text = it.toString()
                    textDidChange(type, text)
                }
            }
        }
    }

    private fun textDidChange(type: FormFieldType, value: String) {
        val validationResults = validators.mapNotNull {
            if (it.fieldType == type) {
                // TODO: to rethink this logic
                if (it is SecurityCodeValidator) {
                    val cardNumber = valueOfFieldWithType(NUMBER)
                    it.cardNetwork = CardNetwork.ofNumber(cardNumber)
                }

                it.validate(value)
            } else null
        }

        val result = validationResults.firstOrNull()

        validationResultsCache[type] = result?.isValid ?: true

        updateSubmitButtonState()

        val layout = textInputLayoutForType(type)
        val isValidResult = result?.isValid ?: true
        val message = context.getString(result?.message ?: R.string.empty)
        val errorEnabled = value.isNotBlank() && !isValidResult && message.isNotEmpty()

        layout?.let {
            it.isErrorEnabled = errorEnabled
            it.error = message
        }
    }

    private fun updateSubmitButtonState() {
        submitButton.isEnabled = model.enabledFields.map {
            validationResultsCache[it] ?: false
        }.reduce { acc, b -> acc && b }
    }

    private fun hintForFieldType(type: FormFieldType): Int = when (type) {
        NUMBER -> R.string.card_number_hint
        HOLDER_NAME -> R.string.card_holder_hint
        EXPIRATION_DATE -> R.string.date_hint
        SECURITY_NUMBER -> R.string.cvv_hint
        COUNTRY -> R.string.country_hint
        POST_CODE -> R.string.post_code_hint
    }

    private fun editTextForType(type: FormFieldType): EditText = when (type) {
        NUMBER -> {
            numberTextInputEditText
        }
        HOLDER_NAME -> {
            nameTextInputEditText
        }
        EXPIRATION_DATE -> {
            expirationDateTextInputEditText
        }
        SECURITY_NUMBER -> {
            securityNumberTextInputEditText
        }
        COUNTRY -> {
            countryTextInputEditText
        }
        POST_CODE -> {
            postcodeTextInputEditText
        }
    }

    private fun textInputLayoutForType(type: FormFieldType): JudoEditTextInputLayout? {
        val editText = editTextForType(type)
        return editText.parentOfType(JudoEditTextInputLayout::class.java)
    }

    private fun update() {
        setupFields()
    }

    private fun setupVisibilityOfFields() {
        val textInputLayouts = subViewsWithType(JudoEditTextInputLayout::class.java)
        textInputLayouts.forEach {
            it.visibility = View.GONE
        }

        model.enabledFields.forEach {
            val textInputLayout = textInputLayoutForType(it)
            textInputLayout?.visibility = View.VISIBLE
        }
    }

    private fun valueOfFieldWithType(type: FormFieldType): String {
        val editText = editTextForType(type)
        return editText.text.toString()
    }

    private fun onSubmit(view: View) {
        val model = FormModel(
                valueOfFieldWithType(NUMBER),
                valueOfFieldWithType(HOLDER_NAME),
                valueOfFieldWithType(EXPIRATION_DATE),
                valueOfFieldWithType(SECURITY_NUMBER),
                valueOfFieldWithType(COUNTRY),
                valueOfFieldWithType(POST_CODE)
        )
        onSubmitListener?.onSubmitForm(this, model)
    }

}