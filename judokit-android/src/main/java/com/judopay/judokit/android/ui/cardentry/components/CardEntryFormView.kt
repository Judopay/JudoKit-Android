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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomappbar.BottomAppBar
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
import com.judopay.judokit.android.ui.common.PATTERN_CARD_EXPIRATION_DATE
import kotlin.collections.forEach as kForEach

interface CardEntryFormListener {
    fun onFieldChanged(
        type: CardDetailsFieldType,
        value: String,
        event: FormFieldEvent,
    )

    fun onSubmit()
}

class CardEntryFormView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : FrameLayout(context, attrs, defStyle) {
        val binding = CardEntryFormViewBinding.inflate(LayoutInflater.from(context), this, true)

        val scrollView: NestedScrollView get() = binding.formScrollView
        val bottomAppBar: BottomAppBar get() = binding.cardEntryBottomAppBar
        val containerLayout: ConstraintLayout get() = binding.cardDetailsContainerLayout

        internal var model = CardDetailsInputModel()
            set(value) {
                val previous = field
                field = value
                val autoTab = pendingAutoTabType.also { pendingAutoTabType = null }
                update(previous, autoTab)
            }

        internal var listener: CardEntryFormListener? = null

        private var pendingAutoTabType: CardDetailsFieldType? = null
        private var isUpdatingSilently = false

        private val countriesAdapter: ArrayAdapter<String> by lazy {
            val countries = AVSCountry.entries.map { context.getString(it.translatableName) }
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
        }

        private fun setupFieldsContent() {
            val scrollView = binding.formScrollView
            binding.cardEntrySubmitButton.setOnClickListener { listener?.onSubmit() }

            CardDetailsFieldType.entries.kForEach { type ->
                editTextForType(type).apply {
                    setHint(type.fieldHintResId)

                    setOnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            val text = valueOfEditTextWithType(type)
                            listener?.onFieldChanged(type, text, FormFieldEvent.FOCUS_CHANGED)
                        } else {
                            scrollView.smoothScrollToView(editTextForType(type))
                        }
                    }
                    addTextChangedListener {
                        if (isUpdatingSilently) return@addTextChangedListener
                        val text = it.toString()
                        pendingAutoTabType = type
                        listener?.onFieldChanged(type, text, FormFieldEvent.TEXT_CHANGED)
                    }
                }
            }
        }

        private fun silentlyUpdate(block: () -> Unit) {
            isUpdatingSilently = true
            try {
                block()
            } finally {
                isUpdatingSilently = false
            }
        }

        private fun update(
            previousModel: CardDetailsInputModel,
            triggeredBy: CardDetailsFieldType?,
        ) {
            updateFieldsVisibility()
            updateFormatters()

            if (model.enabledFields.contains(CardDetailsFieldType.COUNTRY)) {
                setupCountrySpinner()
            }

            binding.cardEntrySubmitButton.state = model.actionButtonState

            preFillFields()
            renderErrors()

            if (triggeredBy != null) {
                val currentErrors = model.fieldErrors
                val prevErrors = previousModel.fieldErrors
                val isNowValid = currentErrors.containsKey(triggeredBy) && currentErrors[triggeredBy] == null
                val wasNotValid = !(prevErrors.containsKey(triggeredBy) && prevErrors[triggeredBy] == null)
                if (isNowValid && wasNotValid) {
                    autoTab(triggeredBy)
                }
            }
        }

        private fun renderErrors() {
            renderFieldErrors(model.fieldErrors, context, ::textInputLayoutForType)
        }

        private fun preFillFields() {
            silentlyUpdate {
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
            }
        }

        private fun updateFormatters() {
            val cardNumber = model.valueOfFieldWithType(CardDetailsFieldType.NUMBER)
            securityCodeFormatter.cardNetwork = model.cardNetwork ?: CardNetwork.ofNumber(cardNumber)
        }

        private fun updateFieldsVisibility() {
            CardDetailsFieldType.entries.kForEach { fieldType ->
                textInputLayoutForType(fieldType)?.let { layout ->
                    val isEnabled = model.enabledFields.contains(fieldType)
                    layout.visibility = if (isEnabled) View.VISIBLE else View.GONE
                }
            }

            val isExpiryDateEnabled = model.enabledFields.contains(CardDetailsFieldType.EXPIRATION_DATE)
            binding.expirationDateHorizontalSpacer.visibility = if (isExpiryDateEnabled) View.VISIBLE else View.GONE
        }

        private fun setupCountrySpinner() =
            binding.countryTextInputEditText.apply {
                setAdapter(countriesAdapter)
                setOnClickListener { showDropDown() }
                setOnItemClickListener { _, _, position, _ ->
                    val selected = AVSCountry.entries[position]
                    onCountryDidSelect(selected)
                }
            }

        private fun autoTab(type: CardDetailsFieldType) {
            if (type == CardDetailsFieldType.HOLDER_NAME || type == CardDetailsFieldType.COUNTRY) return
            val types = CardDetailsFieldType.entries
            val nextIndex = types.indexOf(type) + 1
            if (types.size > nextIndex && model.enabledFields.contains(types[nextIndex])) {
                when (val field = editTextForType(types[nextIndex])) {
                    is AutoCompleteTextView -> {
                        editTextForType(type).clearFocus()
                        field.showDropDown()
                        field.setOnItemClickListener { _, _, _, _ ->
                            val nextAfterDropdown =
                                types
                                    .getOrNull(nextIndex + 1)
                                    ?.takeIf { model.enabledFields.contains(it) }
                            nextAfterDropdown?.let { editTextForType(it).requestFocus() }
                        }
                    }
                    else -> field.requestFocus()
                }
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
    }
