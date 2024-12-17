package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.BillingDetailsFormViewBinding
import com.judopay.judokit.android.dismissKeyboard
import com.judopay.judokit.android.model.AVSCountry
import com.judopay.judokit.android.model.State
import com.judopay.judokit.android.model.canadaProvincesAndTerritories
import com.judopay.judokit.android.model.chinaProvinces
import com.judopay.judokit.android.model.indiaStates
import com.judopay.judokit.android.model.usStates
import com.judopay.judokit.android.parentOfType
import com.judopay.judokit.android.smoothScrollToView
import com.judopay.judokit.android.ui.cardentry.formatting.PhoneCountryCodeTextWatcher
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.model.fieldHintResId
import com.judopay.judokit.android.ui.cardentry.model.valueOfBillingDetailsFieldWithType
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.AddressLineValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.CityValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.EmailValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.MobileNumberValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.PhoneCountryCodeValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CountryValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.PostcodeValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.StateValidator
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CANADA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CHINA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_INDIA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_US

internal typealias BillingDetailsFormValidationStatus = (model: BillingDetailsInputModel, isValid: Boolean) -> Unit
internal typealias BillingDetailsSubmitButtonClickListener = () -> Unit
internal typealias BillingDetailsBackButtonClickListener = () -> Unit

class BillingDetailsFormView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : FrameLayout(context, attrs, defStyle), CardEntryViewAnimator.OnViewWillAppearListener {
        val binding = BillingDetailsFormViewBinding.inflate(LayoutInflater.from(context), this, true)

        internal var onFormValidationStatusListener: BillingDetailsFormValidationStatus? = null
        internal var onBillingDetailsSubmitButtonClickListener: BillingDetailsSubmitButtonClickListener? =
            null
        internal var onBillingDetailsBackButtonClickListener: BillingDetailsBackButtonClickListener? =
            null

        internal var model = BillingDetailsInputModel()
            set(value) {
                field = value
                binding.billingDetailsSubmitButton.state = model.submitButtonState
                binding.billingDetailsBackButton.state = model.backButtonState
            }

        private var validators =
            mutableListOf(
                EmailValidator(),
                CountryValidator(),
                StateValidator(),
                PhoneCountryCodeValidator(),
                MobileNumberValidator(),
                CityValidator(),
                PostcodeValidator(),
                AddressLineValidator(),
            )

        private val phoneNumberFields =
            arrayListOf(BillingDetailsFieldType.PHONE_COUNTRY_CODE.name, BillingDetailsFieldType.MOBILE_NUMBER.name)

        private val countries = Country.list(context)

        private val validationResultsCache = mutableMapOf<BillingDetailsFieldType, Boolean>()

        private val phoneCountryCodeFormatter: PhoneCountryCodeTextWatcher by lazy {
            PhoneCountryCodeTextWatcher()
        }

        private var mobileNumberFormatter: PhoneNumberFormattingTextWatcher? = null
        private var selectedState: State? = null
        private var selectedCountry: Country? = null
            set(value) {
                field = value

                val dialCode = value?.dialCode ?: ""

                textInputLayoutForType(BillingDetailsFieldType.PHONE_COUNTRY_CODE)?.let {
                    it.editText?.setText(dialCode)
                }

                validatorInstance<PostcodeValidator>()?.let {
                    it.country = AVSCountry.entries.firstOrNull { it.name == selectedCountry?.alpha2Code } ?: AVSCountry.OTHER
                }

                validatorInstance<StateValidator>()?.let {
                    it.country = selectedCountry
                }

                setupStateSpinner(selectedCountry)
                updateSubmitButtonState()
            }

        override fun onFinishInflate() {
            super.onFinishInflate()
            setupFieldsContent()
            setupPhoneCountryCodeFormatter()
            setupMobileNumberFormatter()
            setupCountrySpinner()
            setupStateSpinner()
        }

        override fun onViewWillAppear() {
            selectedCountry = countries.firstOrNull { it.numericCode == model.countryCode }

            editTextForType(BillingDetailsFieldType.COUNTRY).apply {
                val text = selectedCountry?.name ?: ""
                setText(text)
                textDidChange(BillingDetailsFieldType.COUNTRY, text, FormFieldEvent.TEXT_CHANGED)
            }
        }

        private fun setupCountrySpinner() =
            binding.countryTextInputEditText.apply {
                val adapter = ArrayAdapter(context, R.layout.country_select_dialog_item, countries)
                setAdapter(adapter)
                setOnItemClickListener { parent, _, index, _ ->
                    selectedCountry = parent.getItemAtPosition(index) as Country
                }
            }

        private fun setupStateSpinner(country: Country? = null) {
            var states = emptyList<State>()
            var hint = R.string.jp_empty
            when (country?.alpha2Code) {
                ALPHA_2_CODE_CANADA -> {
                    states = canadaProvincesAndTerritories
                    hint = R.string.jp_ca_province_hint
                }
                ALPHA_2_CODE_CHINA -> {
                    states = chinaProvinces
                    hint = R.string.jp_cn_province_hint
                }
                ALPHA_2_CODE_INDIA -> {
                    states = indiaStates
                    hint = R.string.jp_in_state_hint
                }
                ALPHA_2_CODE_US -> {
                    states = usStates
                    hint = R.string.jp_us_state_hint
                }
                else -> {
                    validationResultsCache[BillingDetailsFieldType.STATE] = true
                }
            }
            val hasStates = states.isNotEmpty()
            if (hasStates) {
                validationResultsCache[BillingDetailsFieldType.STATE] = false
                binding.stateTextInputEditText.apply {
                    setHint(hint)
                    setAdapter(ArrayAdapter(context, R.layout.country_select_dialog_item, states))
                    setOnItemClickListener { parent, _, position, _ ->
                        selectedState = parent.getItemAtPosition(position) as State
                    }
                }
            }
            selectedState = null
            binding.stateTextInputEditText.setText("")
            binding.stateTextInputLayout.isVisible = hasStates
        }

        private fun setupPhoneCountryCodeFormatter() =
            with(editTextForType(BillingDetailsFieldType.PHONE_COUNTRY_CODE)) {
                addTextChangedListener(phoneCountryCodeFormatter)
            }

        private fun setupMobileNumberFormatter() =
            with(editTextForType(BillingDetailsFieldType.MOBILE_NUMBER)) {
                if (mobileNumberFormatter != null) {
                    removeTextChangedListener(mobileNumberFormatter)
                }
                mobileNumberFormatter =
                    PhoneNumberFormattingTextWatcher(
                        countries.find {
                            it.name == editTextForType(BillingDetailsFieldType.COUNTRY).text.toString()
                        }?.alpha2Code ?: "",
                    )
                addTextChangedListener(mobileNumberFormatter)
            }

        private fun setupFieldsContent() {
            val scrollView = binding.billingDetailsScrollView

            setAddAddressButtonClickListener()
            binding.billingDetailsBackButton.setOnClickListener { onBillingDetailsBackButtonClickListener?.invoke() }
            binding.billingDetailsSubmitButton.setOnClickListener {
                dismissKeyboard()
                onBillingDetailsSubmitButtonClickListener?.invoke()
            }

            BillingDetailsFieldType.entries.forEach { type ->
                editTextForType(type).apply {
                    setHint(type.fieldHintResId)

                    // setup state, and validate it
                    with(model.valueOfBillingDetailsFieldWithType(type)) {
                        setText(this)
                        textDidChange(type, this, FormFieldEvent.TEXT_CHANGED)
                    }

                    if (type == BillingDetailsFieldType.MOBILE_NUMBER) {
                        setOnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                setupMobileNumberFormatter()
                                scrollView.smoothScrollToView(editTextForType(type))
                            } else {
                                val text = valueOfEditTextWithType(type)
                                textDidChange(type, text, FormFieldEvent.FOCUS_CHANGED)
                            }
                        }
                    } else {
                        setOnFocusChangeListener { _, hasFocus ->
                            val text = valueOfEditTextWithType(type)
                            if (!hasFocus) {
                                textDidChange(type, text, FormFieldEvent.FOCUS_CHANGED)
                            } else {
                                scrollView.smoothScrollToView(editTextForType(type))
                            }
                        }
                    }

                    addTextChangedListener {
                        val text = it.toString()
                        textDidChange(type, text, FormFieldEvent.TEXT_CHANGED)
                    }
                }
            }
        }

        @Suppress("MagicNumber")
        private fun setAddAddressButtonClickListener() {
            binding.addAddressLineButton.text = context.getString(R.string.jp_add_address_line, 2)
            binding.addAddressLineButton.setOnClickListener {
                if (binding.addressLine2TextInputLayout.visibility == VISIBLE) {
                    binding.addressLine3TextInputLayout.visibility = VISIBLE
                    binding.addAddressLineButton.visibility = GONE
                } else {
                    binding.addressLine2TextInputLayout.visibility = VISIBLE
                    binding.addAddressLineButton.text = context.getString(R.string.jp_add_address_line, 3)
                }
            }
        }

        private fun editTextForType(type: BillingDetailsFieldType): EditText =
            when (type) {
                BillingDetailsFieldType.COUNTRY -> binding.countryTextInputEditText
                BillingDetailsFieldType.STATE -> binding.stateTextInputEditText
                BillingDetailsFieldType.POST_CODE -> binding.postalCodeTextInputEditText
                BillingDetailsFieldType.EMAIL -> binding.emailTextInputEditText
                BillingDetailsFieldType.PHONE_COUNTRY_CODE -> binding.phoneCountryCodeTextInputEditText
                BillingDetailsFieldType.MOBILE_NUMBER -> binding.mobileNumberTextInputEditText
                BillingDetailsFieldType.ADDRESS_LINE_1 -> binding.addressLine1TextInputEditText
                BillingDetailsFieldType.ADDRESS_LINE_2 -> binding.addressLine2TextInputEditText
                BillingDetailsFieldType.ADDRESS_LINE_3 -> binding.addressLine3TextInputEditText
                BillingDetailsFieldType.CITY -> binding.cityTextInputEditText
            }

        @Suppress("CyclomaticComplexMethod")
        private fun textDidChange(
            type: BillingDetailsFieldType,
            value: String,
            event: FormFieldEvent,
        ) {
            val validationResults =
                validators.mapNotNull {
                    if (it.fieldType == type.name) {
                        if (phoneNumberFields.contains(it.fieldType)) {
                            val code = valueOfEditTextWithType(BillingDetailsFieldType.PHONE_COUNTRY_CODE)
                            val number = valueOfEditTextWithType(BillingDetailsFieldType.MOBILE_NUMBER)
                            when {
                                // in case country code is empty, don't apply the phone validation
                                code.isEmpty() && number.isEmpty() -> return@mapNotNull null
                                code.isEmpty() && number.isNotEmpty() -> return@mapNotNull ValidationResult(
                                    false,
                                    R.string.jp_invalid_phone_country_code,
                                )
                            }
                        }

                        it.validate(value, event)
                    } else {
                        null
                    }
                }
            if (type == BillingDetailsFieldType.COUNTRY && event == FormFieldEvent.TEXT_CHANGED) {
                val typedCountry = countries.firstOrNull { it.name.lowercase() == value.lowercase() }
                if (selectedCountry != typedCountry) {
                    selectedCountry = typedCountry
                }
            }
            if (type == BillingDetailsFieldType.STATE && event == FormFieldEvent.TEXT_CHANGED) {
                val typedState =
                    when (selectedCountry?.alpha2Code) {
                        ALPHA_2_CODE_CANADA -> canadaProvincesAndTerritories.firstOrNull { it.name.lowercase() == value.lowercase() }
                        ALPHA_2_CODE_US -> usStates.firstOrNull { it.name.lowercase() == value.lowercase() }
                        else -> null
                    }
                selectedState = typedState
            }

            val result = validationResults.firstOrNull()

            validationResultsCache[type] = result?.isValid ?: true

            val layout = textInputLayoutForType(type)
            val isValidResult = result?.isValid ?: true
            val message = context.getString(result?.message ?: R.string.jp_empty)
            val errorEnabled = value.isNotBlank() && !isValidResult && message.isNotEmpty()

            layout?.let {
                it.isErrorEnabled = errorEnabled
                it.error = message
            }

            updateSubmitButtonState()
        }

        private fun textInputLayoutForType(type: BillingDetailsFieldType): JudoEditTextInputLayout? {
            val editText = editTextForType(type)
            return editText.parentOfType(JudoEditTextInputLayout::class.java)
        }

        private fun valueOfEditTextWithType(type: BillingDetailsFieldType): String {
            val editText = editTextForType(type)
            return editText.text.toString()
        }

        private fun updateSubmitButtonState() {
            val validationResults =
                BillingDetailsFieldType.entries.map {
                    validationResultsCache[it] ?: false
                }

            var isFormValid = false
            if (validationResults.isNotEmpty()) {
                isFormValid = validationResults.reduce { acc, b -> acc && b }
            }

            onValidationPassed(isFormValid)
        }

        private fun onValidationPassed(isFormValid: Boolean) {
            val inputModel =
                BillingDetailsInputModel(
                    countryCode = selectedCountry?.numericCode ?: "",
                    state = selectedState?.isoCode ?: "",
                    postalCode = valueOfEditTextWithType(BillingDetailsFieldType.POST_CODE),
                    email = valueOfEditTextWithType(BillingDetailsFieldType.EMAIL),
                    addressLine1 = valueOfEditTextWithType(BillingDetailsFieldType.ADDRESS_LINE_1),
                    addressLine2 = valueOfEditTextWithType(BillingDetailsFieldType.ADDRESS_LINE_2),
                    addressLine3 = valueOfEditTextWithType(BillingDetailsFieldType.ADDRESS_LINE_3),
                    phoneCountryCode = valueOfEditTextWithType(BillingDetailsFieldType.PHONE_COUNTRY_CODE),
                    mobileNumber = valueOfEditTextWithType(BillingDetailsFieldType.MOBILE_NUMBER),
                    city = valueOfEditTextWithType(BillingDetailsFieldType.CITY),
                )

            onFormValidationStatusListener?.invoke(inputModel, isFormValid)
        }

        private inline fun <reified V> validatorInstance(): V? {
            return validators.firstOrNull { it is V } as V?
        }
    }
