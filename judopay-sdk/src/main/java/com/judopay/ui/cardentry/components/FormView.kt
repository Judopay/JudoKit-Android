package com.judopay.ui.cardentry.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.judopay.R
import com.judopay.inflate
import com.judopay.model.CardNetwork
import com.judopay.parentOfType
import com.judopay.subViewsWithType
import com.judopay.ui.cardentry.validation.CardHolderNameValidator
import com.judopay.ui.cardentry.validation.CardNumberValidator
import com.judopay.ui.cardentry.validation.ExpirationDateValidator
import com.judopay.ui.cardentry.validation.SecurityCodeValidator
import com.judopay.ui.cardentry.validation.Validator
import com.judopay.ui.common.ButtonState
import kotlinx.android.synthetic.main.form_view.view.*

enum class FormFieldType {
    NUMBER,
    HOLDER_NAME,
    EXPIRATION_DATE,
    SECURITY_NUMBER,
    COUNTRY,
    POST_CODE
}

private val FormFieldType.fieldHintResId: Int
    get() = when (this) {
        FormFieldType.NUMBER -> R.string.card_number_hint
        FormFieldType.HOLDER_NAME -> R.string.card_holder_hint
        FormFieldType.EXPIRATION_DATE -> R.string.date_hint
        FormFieldType.SECURITY_NUMBER -> R.string.cvv_hint
        FormFieldType.COUNTRY -> R.string.country_hint
        FormFieldType.POST_CODE -> R.string.post_code_hint
    }

data class InputModel(
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expirationDate: String = "",
    val securityNumber: String = "",
    val country: String = "",
    val postCode: String = ""
)

data class FormModel(
    val formModel: InputModel,
    val enabledFields: List<FormFieldType>,
    val supportedNetworks: List<CardNetwork>,
    val paymentButtonState: ButtonState = ButtonState.Disabled(R.string.add_card)
)

typealias OnSubmitListener = (model: InputModel) -> Unit
typealias SubmitButtonClickListener = () -> Unit

class FormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.form_view, true)
    }

    var model = FormModel(InputModel(), emptyList(), emptyList())
        set(value) {
            field = value
            update()
        }

    internal var onValidationPassedListener: OnSubmitListener? = null
    internal var onSubmitButtonClickListener: SubmitButtonClickListener? = null

    private val validationResultsCache = mutableMapOf<FormFieldType, Boolean>()
    private val validators: List<Validator> = listOf(
        CardNumberValidator(supportedNetworks = model.supportedNetworks),
        CardHolderNameValidator(),
        ExpirationDateValidator(),
        SecurityCodeValidator()
    )

    override fun onFinishInflate() {
        super.onFinishInflate()

        with(editTextForType(FormFieldType.EXPIRATION_DATE)) {
            val mask = InputMaskTextWatcher(this, "##/##")
            addTextChangedListener(mask)
        }

        val securityCode = editTextForType(FormFieldType.SECURITY_NUMBER)
        val securityCodeMask = SecurityCodeInputMaskTextWatcher(securityCode)
        securityCode.addTextChangedListener(securityCodeMask)

        with(editTextForType(FormFieldType.NUMBER)) {
            val mask = CardNumberInputMaskTextWatcher(this, securityCodeMask)
            addTextChangedListener(mask)
        }

        setupFields()
    }

    private fun setupFields() {
        submitButton.setOnClickListener { onSubmitButtonClickListener?.invoke() }

        FormFieldType.values().forEach { type ->
            editTextForType(type).apply {
                setHint(type.fieldHintResId)

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
                    val cardNumber = valueOfFieldWithType(FormFieldType.NUMBER)
                    it.cardNetwork = CardNetwork.ofNumber(cardNumber)
                }

                it.validate(value)
            } else null
        }

        val result = validationResults.firstOrNull()

        validationResultsCache[type] = result?.isValid ?: true

        val layout = textInputLayoutForType(type)
        val isValidResult = result?.isValid ?: true
        val message = context.getString(result?.message ?: R.string.empty)
        val errorEnabled = value.isNotBlank() && !isValidResult && message.isNotEmpty()

        layout?.let {
            it.isErrorEnabled = errorEnabled
            it.error = message
        }

        updateSubmitButtonState()
    }

    private fun updateSubmitButtonState() {
        val isFormValid = model.enabledFields.map {
            validationResultsCache[it] ?: false
        }.reduce { acc, b -> acc && b }

        if (isFormValid) {
            onValidationPassed()
        }
    }

    private fun editTextForType(type: FormFieldType): EditText = when (type) {
        FormFieldType.NUMBER -> {
            numberTextInputEditText
        }
        FormFieldType.HOLDER_NAME -> {
            nameTextInputEditText
        }
        FormFieldType.EXPIRATION_DATE -> {
            expirationDateTextInputEditText
        }
        FormFieldType.SECURITY_NUMBER -> {
            securityNumberTextInputEditText
        }
        FormFieldType.COUNTRY -> {
            countryTextInputEditText
        }
        FormFieldType.POST_CODE -> {
            postcodeTextInputEditText
        }
    }

    private fun textInputLayoutForType(type: FormFieldType): JudoEditTextInputLayout? {
        val editText = editTextForType(type)
        return editText.parentOfType(JudoEditTextInputLayout::class.java)
    }

    private fun update() {
        setupVisibilityOfFields()
        submitButton.state = model.paymentButtonState
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

    private fun onValidationPassed() {
        val model = InputModel(
            valueOfFieldWithType(FormFieldType.NUMBER),
            valueOfFieldWithType(FormFieldType.HOLDER_NAME),
            valueOfFieldWithType(FormFieldType.EXPIRATION_DATE),
            valueOfFieldWithType(FormFieldType.SECURITY_NUMBER),
            valueOfFieldWithType(FormFieldType.COUNTRY),
            valueOfFieldWithType(FormFieldType.POST_CODE)
        )
        onValidationPassedListener?.invoke(model)
    }
}
