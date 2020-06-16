package com.judokit.android.ui.cardentry.components

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.judokit.android.R
import com.judokit.android.inflate
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.Country
import com.judokit.android.model.asCountry
import com.judokit.android.model.displayName
import com.judokit.android.model.postcodeMaxLength
import com.judokit.android.parentOfType
import com.judokit.android.ui.cardentry.formatting.CardNumberInputMaskTextWatcher
import com.judokit.android.ui.cardentry.formatting.InputMaskTextWatcher
import com.judokit.android.ui.cardentry.formatting.SecurityCodeInputMaskTextWatcher
import com.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judokit.android.ui.cardentry.model.FormFieldType
import com.judokit.android.ui.cardentry.model.FormModel
import com.judokit.android.ui.cardentry.model.InputModel
import com.judokit.android.ui.cardentry.model.fieldHintResId
import com.judokit.android.ui.cardentry.model.valueOfFieldWithType
import com.judokit.android.ui.cardentry.validation.CardHolderNameValidator
import com.judokit.android.ui.cardentry.validation.CardNumberValidator
import com.judokit.android.ui.cardentry.validation.CountryValidator
import com.judokit.android.ui.cardentry.validation.ExpirationDateValidator
import com.judokit.android.ui.cardentry.validation.PostcodeValidator
import com.judokit.android.ui.cardentry.validation.SecurityCodeValidator
import com.judokit.android.ui.common.PATTERN_CARD_EXPIRATION_DATE
import kotlinx.android.synthetic.main.form_view.view.*

internal typealias OnSubmitListener = (model: InputModel) -> Unit
internal typealias SubmitButtonClickListener = () -> Unit

class FormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.form_view, true)
    }

    internal var model = FormModel(
        InputModel(),
        emptyList(),
        emptyList()
    )
        set(value) {
            field = value
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

    private val countriesAdapter: ArrayAdapter<String> by lazy {
        val countries = Country.values().map { it.displayName }
        ArrayAdapter(context, android.R.layout.simple_list_item_1, countries)
    }

    private val securityCodeFormatter: SecurityCodeInputMaskTextWatcher by lazy {
        val editText = editTextForType(FormFieldType.SECURITY_NUMBER)
        SecurityCodeInputMaskTextWatcher(editText).also { it.cardNetwork = model.cardNetwork }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupFieldsContent()
        setupFieldsFormatting()
    }

    private fun setupFieldsFormatting() {
        setupExpirationDateFormatting()
        setupCountryFormatter()

        setupSecurityCodeFormatter()
        setupNumberFormatter()
    }

    private fun setupCountryFormatter() {
        val country = model.valueOfFieldWithType(FormFieldType.COUNTRY).asCountry()
        onCountryDidSelect(country ?: Country.OTHER)
    }

    private fun setupNumberFormatter() =
        with(editTextForType(FormFieldType.NUMBER)) {
            val mask = CardNumberInputMaskTextWatcher(
                this,
                securityCodeFormatter,
                model.cardNetwork
            )
            addTextChangedListener(mask)
        }

    private fun setupSecurityCodeFormatter() =
        with(editTextForType(FormFieldType.SECURITY_NUMBER)) {
            addTextChangedListener(securityCodeFormatter)
        }

    private fun setupExpirationDateFormatting() =
        with(editTextForType(FormFieldType.EXPIRATION_DATE)) {
            val mask = InputMaskTextWatcher(this, PATTERN_CARD_EXPIRATION_DATE)
            addTextChangedListener(mask)
        }

    private fun onCountryDidSelect(country: Country) {
        val previousSelected =
            model.valueOfFieldWithType(FormFieldType.COUNTRY).asCountry()
        val postCodeEditText = editTextForType(FormFieldType.POST_CODE)

        postCodeEditText.filters = arrayOf(InputFilter.LengthFilter(country.postcodeMaxLength))

        if (country != previousSelected) {
            postCodeEditText.text.clear()
        }

        validatorInstance<PostcodeValidator>()?.country = country
    }

    private fun setupFieldsContent() {
        submitButton.setOnClickListener { onSubmitButtonClickListener?.invoke() }

        FormFieldType.values().forEach { type ->
            editTextForType(type).apply {
                setHint(type.fieldHintResId)

                // setup state, and validate it
                with(model.valueOfFieldWithType(type)) {
                    setText(this)
                    textDidChange(type, this, FormFieldEvent.TEXT_CHANGED)
                }

                if (type == FormFieldType.SECURITY_NUMBER) {
                    setOnFocusChangeListener { _, hasFocus ->
                        val text = valueOfEditTextWithType(type)
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
        if (type == FormFieldType.NUMBER) {
            validatorInstance<SecurityCodeValidator>()?.let {
                it.cardNetwork = model.cardNetwork ?: CardNetwork.ofNumber(value)
            }
        }

        val validationResults = validators.mapNotNull {
            if (it.fieldType == type) {
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
        if (isValidResult && type != FormFieldType.HOLDER_NAME && type != FormFieldType.COUNTRY) {
            val types = FormFieldType.values().toList()
            val nextFormFieldType = types.indexOf(type) + 1
            if (types.size > nextFormFieldType) {
                when (val field = editTextForType(types[nextFormFieldType])) {
                    is AutoCompleteTextView -> {
                        editTextForType(type).clearFocus()
                        field.showDropDown()
                        field.setOnItemClickListener { _, _, _, _ ->
                            editTextForType(FormFieldType.POST_CODE).requestFocus()
                        }
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
            submitButton.state = model.paymentButtonState
        }
    }

    private fun update() {
        updateValidators()
        updateFieldsVisibility()
        updateFormatters()

        if (model.enabledFields.contains(FormFieldType.COUNTRY)) {
            setupCountrySpinner()
        }

        submitButton.state = model.paymentButtonState

        preFillFields()
    }

    private fun preFillFields() = model.enabledFields.forEach {
        val valueToFrom = valueOfEditTextWithType(it)
        val valueToUpdateTo = model.valueOfFieldWithType(it)
        if (valueToFrom != valueToUpdateTo) {
            with(editTextForType(it).text) {
                clear()
                append(valueToUpdateTo)
            }
        }
    }

    private fun updateValidators() {
        validatorInstance<CardNumberValidator>()?.let {
            it.supportedNetworks = model.supportedNetworks
        }
    }

    private fun updateFormatters() {
        val cardNumber = model.valueOfFieldWithType(FormFieldType.NUMBER)
        securityCodeFormatter.cardNetwork = model.cardNetwork ?: CardNetwork.ofNumber(cardNumber)
    }

    private fun updateFieldsVisibility() {
        FormFieldType.values().forEach { fieldType ->
            textInputLayoutForType(fieldType)?.let { layout ->
                val isEnabled = model.enabledFields.contains(fieldType)
                layout.visibility = if (isEnabled) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupCountrySpinner() = countryTextInputEditText.apply {
        setAdapter(countriesAdapter)
        setOnClickListener { showDropDown() }
        setOnItemClickListener { _, _, _, id ->
            val selected = Country.values()[id.toInt()]
            onCountryDidSelect(selected)
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

    private fun valueOfEditTextWithType(type: FormFieldType): String {
        val editText = editTextForType(type)
        return editText.text.toString()
    }

    private fun onValidationPassed() {
        val inputModel = InputModel(
            valueOfEditTextWithType(FormFieldType.NUMBER),
            valueOfEditTextWithType(FormFieldType.HOLDER_NAME),
            valueOfEditTextWithType(FormFieldType.EXPIRATION_DATE),
            valueOfEditTextWithType(FormFieldType.SECURITY_NUMBER),
            valueOfEditTextWithType(FormFieldType.COUNTRY),
            valueOfEditTextWithType(FormFieldType.POST_CODE)
        )

        onValidationPassedListener?.invoke(inputModel)
    }

    private inline fun <reified V> validatorInstance(): V? {
        return validators.firstOrNull { it is V } as V?
    }
}
