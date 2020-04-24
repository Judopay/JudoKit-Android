package com.judopay.ui.cardentry.components

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.judopay.R
import com.judopay.inflate
import com.judopay.model.CardNetwork
import com.judopay.model.Country
import com.judopay.model.asCountry
import com.judopay.model.displayName
import com.judopay.model.postcodeMaxLength
import com.judopay.parentOfType
import com.judopay.subViewsWithType
import com.judopay.ui.cardentry.formatting.CardNumberInputMaskTextWatcher
import com.judopay.ui.cardentry.formatting.InputMaskTextWatcher
import com.judopay.ui.cardentry.formatting.SecurityCodeInputMaskTextWatcher
import com.judopay.ui.cardentry.validation.CardHolderNameValidator
import com.judopay.ui.cardentry.validation.CardNumberValidator
import com.judopay.ui.cardentry.validation.CountryValidator
import com.judopay.ui.cardentry.validation.ExpirationDateValidator
import com.judopay.ui.cardentry.validation.PostcodeValidator
import com.judopay.ui.cardentry.validation.SecurityCodeValidator
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

enum class FormFieldEvent {
    TEXT_CHANGED,
    FOCUS_CHANGED
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
    val country: String = Country.GB.displayName,
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

    var submitButtonText: Int = R.string.pay_now
    var model = FormModel(InputModel(), emptyList(), emptyList())
        set(value) {
            field = value
            validators.filterIsInstance<CardNumberValidator>()
                .firstOrNull()
                ?.supportedNetworks = value.supportedNetworks
            update()
        }

    internal var onValidationPassedListener: OnSubmitListener? = null
    internal var onSubmitButtonClickListener: SubmitButtonClickListener? = null

    private val validationResultsCache = mutableMapOf<FormFieldType, Boolean>()
    private var validators = mutableListOf(
        CardNumberValidator(supportedNetworks = model.supportedNetworks),
        CardHolderNameValidator(),
        ExpirationDateValidator(),
        SecurityCodeValidator(),
        CountryValidator(),
        PostcodeValidator()
    )

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupFieldsFormatting()
        setupFieldsContent()
    }

    private fun setupFieldsFormatting() {
        // expiration date field formatting
        with(editTextForType(FormFieldType.EXPIRATION_DATE)) {
            val mask = InputMaskTextWatcher(
                this,
                "##/##"
            )
            addTextChangedListener(mask)
        }

        // security code field formatting
        val securityCode = editTextForType(FormFieldType.SECURITY_NUMBER)
        val securityCodeMask =
            SecurityCodeInputMaskTextWatcher(
                securityCode
            )
        securityCode.addTextChangedListener(securityCodeMask)

        // card number field formatting
        with(editTextForType(FormFieldType.NUMBER)) {
            val mask =
                CardNumberInputMaskTextWatcher(
                    this,
                    securityCodeMask
                )
            addTextChangedListener(mask)
        }

        // Postcode formatting
        val country =
            inputModelValueOfFieldWithType(FormFieldType.COUNTRY).asCountry() ?: Country.OTHER
        onCountryDidSelect(country)

        countryTextInputEditText.setOnItemClickListener { _, _, _, id ->
            val selected = Country.values()[id.toInt()]
            onCountryDidSelect(selected)
        }
    }

    private fun onCountryDidSelect(country: Country) {
        val previousSelected = inputModelValueOfFieldWithType(FormFieldType.COUNTRY).asCountry()
        val postCodeEditText = editTextForType(FormFieldType.POST_CODE)

        postCodeEditText.filters = arrayOf(InputFilter.LengthFilter(country.postcodeMaxLength))

        if (country != previousSelected) {
            postCodeEditText.setText(R.string.empty)
        }

        validators.forEach {
            if (it is PostcodeValidator) {
                it.country = country
            }
        }
    }

    private fun setupFieldsContent() {
        submitButton.setOnClickListener { onSubmitButtonClickListener?.invoke() }

        FormFieldType.values().forEach { type ->
            editTextForType(type).apply {
                setHint(type.fieldHintResId)

                // setup state, and validate it
                with(inputModelValueOfFieldWithType(type)) {
                    setText(this)
                    textDidChange(type, this, FormFieldEvent.TEXT_CHANGED)
                }

                if (type == FormFieldType.SECURITY_NUMBER) {
                    setOnFocusChangeListener { v, hasFocus ->
                        val text = valueOfFieldWithType(type)
                        if (!hasFocus) textDidChange(type, text, FormFieldEvent.FOCUS_CHANGED)
                    }
                }
                addTextChangedListener {
                    val text = it.toString()
                    textDidChange(type, text, FormFieldEvent.TEXT_CHANGED)
                }
            }
        }
    }

    private fun textDidChange(type: FormFieldType, value: String, event: FormFieldEvent) {
        val validationResults = validators.mapNotNull {
            if (it.fieldType == type) {
                // TODO: to rethink this logic
                if (it is SecurityCodeValidator) {
                    val cardNumber = valueOfFieldWithType(FormFieldType.NUMBER)
                    it.cardNetwork = CardNetwork.ofNumber(cardNumber)
                }
                it.validate(value, event)
            } else null
        }

        val result = validationResults.firstOrNull()

        validationResultsCache[type] = result?.isValid ?: true

        val layout = textInputLayoutForType(type)
        val isValidResult = result?.isValid ?: true
        val message = context.getString(result?.message ?: R.string.empty)
        val errorEnabled = value.isNotBlank() && !isValidResult && message.isNotEmpty()

        if (event == FormFieldEvent.TEXT_CHANGED)
            autoTab(isValidResult, type)

        layout?.let {
            it.isErrorEnabled = errorEnabled
            it.error = message
        }
        updateSubmitButtonState()
    }

    private fun autoTab(isValidResult: Boolean, type: FormFieldType) {
        if (isValidResult && type != FormFieldType.HOLDER_NAME) {
            val types = FormFieldType.values().toList()
            val nextFormFieldType = types.indexOf(type) + 1
            if (types.size > nextFormFieldType) {
                when (val field = editTextForType(types[nextFormFieldType])) {
                    is AutoCompleteTextView -> {
                        editTextForType(FormFieldType.POST_CODE).requestFocus()
                        field.showDropDown()
                    }
                    else -> field.requestFocus()
                }
            }
        }
    }

    private fun updateSubmitButtonState() {
        val validationResults = model.enabledFields.map {
            validationResultsCache[it] ?: false
        }

        var isFormValid = false
        if (validationResults.isNotEmpty()) {
            isFormValid = validationResults.reduce { acc, b -> acc && b }
        }

        if (isFormValid) {
            onValidationPassed()
        } else {
            submitButton.state = ButtonState.Disabled(submitButtonText)
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

        model.enabledFields.forEach {
            val field = editTextForType(it)
            val value = inputModelValueOfFieldWithType(it)
            field.setText(value)
        }
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

        if (model.enabledFields.contains(FormFieldType.COUNTRY)) {
            val countries = Country.values().map { it.displayName }
            val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, countries)

            countryTextInputEditText.apply {
                setAdapter(adapter)
                setOnClickListener { showDropDown() }
            }
        }
    }

    private fun inputModelValueOfFieldWithType(type: FormFieldType): String = when (type) {
        FormFieldType.NUMBER -> model.formModel.cardNumber
        FormFieldType.HOLDER_NAME -> model.formModel.cardHolderName
        FormFieldType.EXPIRATION_DATE -> model.formModel.expirationDate
        FormFieldType.SECURITY_NUMBER -> model.formModel.securityNumber
        FormFieldType.COUNTRY -> model.formModel.country
        FormFieldType.POST_CODE -> model.formModel.postCode
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
