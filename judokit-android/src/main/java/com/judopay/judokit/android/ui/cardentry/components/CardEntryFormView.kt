package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.CardEntryFormViewBinding
import com.judopay.judokit.android.model.AVSCountry
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.asAVSCountry
import com.judopay.judokit.android.model.postcodeMaxLength
import com.judopay.judokit.android.model.translatableName
import com.judopay.judokit.android.parentOfType
import com.judopay.judokit.android.smoothScrollToView
import com.judopay.judokit.android.ui.cardentry.formatting.CardNumberInputMaskTextWatcher
import com.judopay.judokit.android.ui.cardentry.formatting.InputMaskTextWatcher
import com.judopay.judokit.android.ui.cardentry.formatting.SecurityCodeInputMaskTextWatcher
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.model.fieldHintResId
import com.judopay.judokit.android.ui.cardentry.model.valueOfFieldWithType
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CardHolderNameValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CardNumberValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CountryValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.ExpirationDateValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.PostcodeValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.SecurityCodeValidator
import com.judopay.judokit.android.ui.common.PATTERN_CARD_EXPIRATION_DATE
import com.judopay.judokit.android.ui.common.heightWithInsetsAndMargins
import kotlin.collections.forEach as kForEach

internal typealias FormValidationStatus = (model: CardDetailsInputModel, isValid: Boolean) -> Unit
internal typealias CardEntryButtonClickListener = () -> Unit

class CardEntryFormView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : FrameLayout(context, attrs, defStyle) {
        private val binding = CardEntryFormViewBinding.inflate(LayoutInflater.from(context), this, true)

        internal var model = CardDetailsInputModel()
            set(value) {
                field = value
                update()
            }

        internal var onFormValidationStatusListener: FormValidationStatus? = null
        internal var onCardEntryButtonClickListener: CardEntryButtonClickListener? = null

        private val validationResultsCache = mutableMapOf<CardDetailsFieldType, Boolean>()
        private var validators =
            mutableListOf(
                CardNumberValidator(supportedNetworks = model.supportedNetworks),
                CardHolderNameValidator(),
                ExpirationDateValidator(),
                SecurityCodeValidator(model.cardNetwork),
                CountryValidator(),
                PostcodeValidator(),
            )

        private val countriesAdapter: ArrayAdapter<String> by lazy {
            val countries = AVSCountry.values().map { context.getString(it.translatableName) }
            ArrayAdapter(context, android.R.layout.simple_list_item_1, countries)
        }

        private val securityCodeFormatter: SecurityCodeInputMaskTextWatcher by lazy {
            val editText = editTextForType(CardDetailsFieldType.SECURITY_NUMBER)
            SecurityCodeInputMaskTextWatcher(editText).also { it.cardNetwork = model.cardNetwork }
        }

        override fun onFinishInflate() {
            super.onFinishInflate()
            setupFieldsContent()
            setupFieldsFormatting()

            binding.cardEntryBottomAppBar.post {
                val additionalSpacing = resources.getDimension(R.dimen.space_8).toInt()
                val params = binding.cardDetailsContainerLayout.layoutParams as LayoutParams
                params.bottomMargin = binding.cardEntryBottomAppBar.heightWithInsetsAndMargins + additionalSpacing
                binding.cardDetailsContainerLayout.layoutParams = params
            }
        }

        private fun setupFieldsFormatting() {
            setupExpirationDateFormatter()
            setupCountryFormatter()
            setupSecurityCodeFormatter()
            setupNumberFormatter()
        }

        private fun setupCountryFormatter() {
            val country = model.valueOfFieldWithType(CardDetailsFieldType.COUNTRY).asAVSCountry()
            onCountryDidSelect(country ?: AVSCountry.OTHER)
        }

        private fun setupNumberFormatter() =
            with(editTextForType(CardDetailsFieldType.NUMBER)) {
                val mask =
                    CardNumberInputMaskTextWatcher(
                        this,
                        securityCodeFormatter,
                        model.cardNetwork,
                    )
                addTextChangedListener(mask)
            }

        private fun setupSecurityCodeFormatter() =
            with(editTextForType(CardDetailsFieldType.SECURITY_NUMBER)) {
                addTextChangedListener(securityCodeFormatter)
            }

        private fun setupExpirationDateFormatter() =
            with(editTextForType(CardDetailsFieldType.EXPIRATION_DATE)) {
                val mask = InputMaskTextWatcher(this, PATTERN_CARD_EXPIRATION_DATE)
                addTextChangedListener(mask)
            }

        private fun onCountryDidSelect(country: AVSCountry) {
            val previousSelected =
                model.valueOfFieldWithType(CardDetailsFieldType.COUNTRY).asAVSCountry()
            val postCodeEditText = editTextForType(CardDetailsFieldType.POST_CODE)

            postCodeEditText.filters = arrayOf(InputFilter.LengthFilter(country.postcodeMaxLength))

            if (country != previousSelected) {
                postCodeEditText.text.clear()
            }

            validatorInstance<PostcodeValidator>()?.country = country
        }

        private fun setupFieldsContent() {
            val scrollView = binding.formScrollView
            binding.cardEntrySubmitButton.setOnClickListener { onCardEntryButtonClickListener?.invoke() }

            CardDetailsFieldType.values().kForEach { type ->
                editTextForType(type).apply {
                    setHint(type.fieldHintResId)

                    // setup state, and validate it
                    with(model.valueOfFieldWithType(type)) {
                        setText(this)
                        textDidChange(type, this, FormFieldEvent.TEXT_CHANGED)
                    }

                    setOnFocusChangeListener { _, hasFocus ->
                        val text = valueOfEditTextWithType(type)
                        if (!hasFocus) {
                            textDidChange(type, text, FormFieldEvent.FOCUS_CHANGED)
                        } else {
                            scrollView.smoothScrollToView(editTextForType(type))
                        }
                    }
                    addTextChangedListener {
                        val text = it.toString()
                        textDidChange(type, text, FormFieldEvent.TEXT_CHANGED)
                    }
                }
            }
        }

        private fun textDidChange(
            type: CardDetailsFieldType,
            value: String,
            event: FormFieldEvent,
        ) {
            var hasCardNetworkChanged = false
            if (type == CardDetailsFieldType.NUMBER) {
                validatorInstance<SecurityCodeValidator>()?.let {
                    val newCardNetwork = model.cardNetwork ?: CardNetwork.ofNumber(value)
                    hasCardNetworkChanged = it.cardNetwork != newCardNetwork
                    if (hasCardNetworkChanged) {
                        it.cardNetwork = newCardNetwork
                    }
                }
            }

            val validationResults =
                validators.mapNotNull {
                    if (it.fieldType == type.name) {
                        it.validate(value, event)
                    } else {
                        null
                    }
                }

            val result = validationResults.firstOrNull()

            validationResultsCache[type] = result?.isValid ?: true

            val layout = textInputLayoutForType(type)
            val isValidResult = result?.isValid ?: true
            val message = context.getString(result?.message ?: R.string.jp_empty)
            val errorEnabled = value.isNotBlank() && !isValidResult && message.isNotEmpty()

            if (event == FormFieldEvent.TEXT_CHANGED) {
                autoTab(isValidResult, type)
            }

            layout?.let {
                it.isErrorEnabled = errorEnabled
                it.error = message
            }
            updateSubmitButtonState()

            if (hasCardNetworkChanged) {
                textDidChange(
                    CardDetailsFieldType.SECURITY_NUMBER,
                    valueOfEditTextWithType(CardDetailsFieldType.SECURITY_NUMBER),
                    FormFieldEvent.FOCUS_CHANGED,
                )
            }
        }

        private fun autoTab(
            isValidResult: Boolean,
            type: CardDetailsFieldType,
        ) {
            if (isValidResult && type != CardDetailsFieldType.HOLDER_NAME && type != CardDetailsFieldType.COUNTRY) {
                val types = CardDetailsFieldType.values().toList()
                val nextFormFieldType = types.indexOf(type) + 1
                if (types.size > nextFormFieldType && model.enabledFields.contains(types[nextFormFieldType])) {
                    when (val field = editTextForType(types[nextFormFieldType])) {
                        is AutoCompleteTextView -> {
                            editTextForType(type).clearFocus()
                            field.showDropDown()
                            field.setOnItemClickListener { _, _, _, _ ->
                                editTextForType(CardDetailsFieldType.POST_CODE).requestFocus()
                            }
                        }
                        else -> {
                            field.requestFocus()
                        }
                    }
                }
            }
        }

        private fun updateSubmitButtonState() {
            val validationResults =
                model.enabledFields.map {
                    validationResultsCache[it] ?: false
                }

            var isFormValid = false
            if (validationResults.isNotEmpty()) {
                isFormValid = validationResults.reduce { acc, b -> acc && b }
            }

            onValidationPassed(isFormValid)
        }

        private fun update() {
            updateValidators()
            updateFieldsVisibility()
            updateFormatters()

            if (model.enabledFields.contains(CardDetailsFieldType.COUNTRY)) {
                setupCountrySpinner()
            }

            binding.cardEntrySubmitButton.state = model.actionButtonState

            preFillFields()
        }

        private fun preFillFields() =
            model.enabledFields.kForEach { field ->
                val valueToFrom = valueOfEditTextWithType(field)
                val valueToUpdateTo = model.valueOfFieldWithType(field)
                if (valueToFrom != valueToUpdateTo) {
                    with(editTextForType(field).text) {
                        clear()
                        append(valueToUpdateTo)
                    }
                }
            }

        private fun updateValidators() {
            validatorInstance<CardNumberValidator>()?.let {
                it.supportedNetworks = model.supportedNetworks
            }
            validatorInstance<SecurityCodeValidator>()?.let {
                val cardNumber = model.valueOfFieldWithType(CardDetailsFieldType.NUMBER)
                it.cardNetwork = model.cardNetwork ?: CardNetwork.ofNumber(cardNumber)
            }
        }

        private fun updateFormatters() {
            val cardNumber = model.valueOfFieldWithType(CardDetailsFieldType.NUMBER)
            securityCodeFormatter.cardNetwork = model.cardNetwork ?: CardNetwork.ofNumber(cardNumber)
        }

        private fun updateFieldsVisibility() {
            CardDetailsFieldType.values().kForEach { fieldType ->
                textInputLayoutForType(fieldType)?.let { layout ->
                    val isEnabled = model.enabledFields.contains(fieldType)
                    layout.visibility = if (isEnabled) View.VISIBLE else View.GONE
                }
            }
        }

        private fun setupCountrySpinner() =
            binding.countryTextInputEditText.apply {
                setAdapter(countriesAdapter)
                setOnClickListener { showDropDown() }
                setOnItemClickListener { _, _, _, id ->
                    val selected = AVSCountry.values()[id.toInt()]
                    onCountryDidSelect(selected)
                }
            }

        private fun editTextForType(type: CardDetailsFieldType): EditText =
            when (type) {
                CardDetailsFieldType.NUMBER -> binding.numberTextInputEditText
                CardDetailsFieldType.HOLDER_NAME -> binding.nameTextInputEditText
                CardDetailsFieldType.EXPIRATION_DATE -> binding.expirationDateTextInputEditText
                CardDetailsFieldType.SECURITY_NUMBER -> binding.securityNumberTextInputEditText
                CardDetailsFieldType.COUNTRY -> binding.countryTextInputEditText
                CardDetailsFieldType.POST_CODE -> binding.postcodeTextInputEditText
            }

        private fun textInputLayoutForType(type: CardDetailsFieldType): JudoEditTextInputLayout? {
            val editText = editTextForType(type)
            return editText.parentOfType(JudoEditTextInputLayout::class.java)
        }

        private fun valueOfEditTextWithType(type: CardDetailsFieldType): String {
            val editText = editTextForType(type)
            return editText.text.toString()
        }

        private fun onValidationPassed(isFormValid: Boolean) {
            val inputModel =
                CardDetailsInputModel(
                    valueOfEditTextWithType(CardDetailsFieldType.NUMBER),
                    valueOfEditTextWithType(CardDetailsFieldType.HOLDER_NAME),
                    valueOfEditTextWithType(CardDetailsFieldType.EXPIRATION_DATE),
                    valueOfEditTextWithType(CardDetailsFieldType.SECURITY_NUMBER),
                    valueOfEditTextWithType(CardDetailsFieldType.COUNTRY),
                    valueOfEditTextWithType(CardDetailsFieldType.POST_CODE),
                )

            onFormValidationStatusListener?.invoke(inputModel, isFormValid)
        }

        private inline fun <reified V> validatorInstance(): V? = validators.firstOrNull { it is V } as V?
    }
