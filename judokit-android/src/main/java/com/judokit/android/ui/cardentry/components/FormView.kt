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
import com.judokit.android.model.securityCodeNameOfCardNetwork
import com.judokit.android.model.securityCodeNumberMaskOfCardNetwork
import com.judokit.android.parentOfType
import com.judokit.android.subViewsWithType
import com.judokit.android.ui.cardentry.formatting.CardNumberInputMaskTextWatcher
import com.judokit.android.ui.cardentry.formatting.InputMaskTextWatcher
import com.judokit.android.ui.cardentry.formatting.SecurityCodeInputMaskTextWatcher
import com.judokit.android.ui.cardentry.validation.CardHolderNameValidator
import com.judokit.android.ui.cardentry.validation.CardNumberValidator
import com.judokit.android.ui.cardentry.validation.CountryValidator
import com.judokit.android.ui.cardentry.validation.ExpirationDateValidator
import com.judokit.android.ui.cardentry.validation.PostcodeValidator
import com.judokit.android.ui.cardentry.validation.SecurityCodeValidator
import com.judokit.android.ui.common.ButtonState
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
    val paymentButtonState: ButtonState = ButtonState.Disabled(R.string.add_card),
    val cardNetwork: CardNetwork? = null
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
    var amount: String? = null
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

    private var securityCodeWatcher: SecurityCodeInputMaskTextWatcher? = null
    private var cardNumberWatcher: CardNumberInputMaskTextWatcher? = null
    private var expirationDateWatcher: InputMaskTextWatcher? = null

    private val validationResultsCache = mutableMapOf<FormFieldType, Boolean>()
    private var validators = mutableListOf(
        CardNumberValidator(supportedNetworks = model.supportedNetworks),
        CardHolderNameValidator(),
        ExpirationDateValidator(),
        SecurityCodeValidator(),
        CountryValidator(),
        PostcodeValidator()
    )
    private var isFormValid = false

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupFieldsContent()
    }

    private fun setupFieldsFormatting() {
        addSecurityCodeFormatter()
        model.enabledFields.forEach {
            when (it) {
                FormFieldType.EXPIRATION_DATE -> addExpirationDateFormatting()
                FormFieldType.NUMBER -> addNumberFormatter()
                FormFieldType.COUNTRY -> addCountryFormatter()
                FormFieldType.SECURITY_NUMBER -> {
                    if (model.cardNetwork != null) {
                        securityCodeWatcher?.apply {
                            hint = model.cardNetwork.securityCodeNameOfCardNetwork
                            mask = model.cardNetwork.securityCodeNumberMaskOfCardNetwork
                        }
                    }
                }
                else -> return@forEach
            }
        }
    }

    private fun addCountryFormatter() {
        val country =
            inputModelValueOfFieldWithType(FormFieldType.COUNTRY).asCountry() ?: Country.OTHER
        onCountryDidSelect(country)

        countryTextInputEditText.setOnItemClickListener { _, _, _, id ->
            val selected = Country.values()[id.toInt()]
            onCountryDidSelect(selected)
        }
    }

    private fun addNumberFormatter() {
        with(editTextForType(FormFieldType.NUMBER)) {
            if (cardNumberWatcher != null) {
                removeTextChangedListener(cardNumberWatcher)
            }
            val mask =
                CardNumberInputMaskTextWatcher(
                    this,
                    securityCodeWatcher,
                    model.cardNetwork
                )
            addTextChangedListener(mask)
            cardNumberWatcher = mask
        }
    }

    private fun addSecurityCodeFormatter() {
        with(editTextForType(FormFieldType.SECURITY_NUMBER)) {
            if (securityCodeWatcher != null) {
                removeTextChangedListener(securityCodeWatcher)
            }
            val mask = SecurityCodeInputMaskTextWatcher(this)
            addTextChangedListener(mask)
            securityCodeWatcher = mask
        }
    }

    private fun addExpirationDateFormatting() {
        with(editTextForType(FormFieldType.EXPIRATION_DATE)) {
            if (expirationDateWatcher != null) {
                removeTextChangedListener(expirationDateWatcher)
            }
            val mask = InputMaskTextWatcher(
                this,
                "##/##"
            )
            addTextChangedListener(mask)
            expirationDateWatcher = mask
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
                    setOnFocusChangeListener { _, hasFocus ->
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
                    if (model.cardNetwork != null) {
                        it.cardNetwork = model.cardNetwork
                    } else {
                        val cardNumber = valueOfFieldWithType(FormFieldType.NUMBER)
                        it.cardNetwork = CardNetwork.ofNumber(cardNumber)
                    }
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
            submitButton.state = ButtonState.Disabled(submitButtonText, amount)
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
        setupFieldsFormatting()
        setupVisibilityOfFields()
        submitButton.state = model.paymentButtonState

        if (!isFormValid) {
            val avsFields = listOf(FormFieldType.COUNTRY, FormFieldType.POST_CODE)
            model.enabledFields.filter { field -> field !in avsFields }
                .forEach {
                    val field = editTextForType(it)
                    val value = inputModelValueOfFieldWithType(it)
                    field.setText(value)
                }
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
        isFormValid = true
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
