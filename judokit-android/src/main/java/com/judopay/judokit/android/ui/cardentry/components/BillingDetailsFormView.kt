package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.os.Build
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.judopay.judokit.android.R
import com.judopay.judokit.android.dismissKeyboard
import com.judopay.judokit.android.inflate
import com.judopay.judokit.android.model.Country
import com.judopay.judokit.android.model.State
import com.judopay.judokit.android.model.canadaProvincesAndTerritories
import com.judopay.judokit.android.model.usStates
import com.judopay.judokit.android.parentOfType
import com.judopay.judokit.android.smoothScrollToView
import com.judopay.judokit.android.ui.cardentry.formatting.PhoneCountryCodeTextWatcher
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.CountryInfo
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
import kotlinx.android.synthetic.main.billing_details_form_view.view.*

internal typealias BillingDetailsFormValidationStatus = (model: BillingDetailsInputModel, isValid: Boolean) -> Unit
internal typealias BillingDetailsSubmitButtonClickListener = () -> Unit
internal typealias BillingDetailsBackButtonClickListener = () -> Unit

class BillingDetailsFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.billing_details_form_view, true)
    }

    internal var onFormValidationStatusListener: BillingDetailsFormValidationStatus? = null
    internal var onBillingDetailsSubmitButtonClickListener: BillingDetailsSubmitButtonClickListener? =
        null
    internal var onBillingDetailsBackButtonClickListener: BillingDetailsBackButtonClickListener? =
        null

    internal var model = BillingDetailsInputModel()
        set(value) {
            field = value
            billingDetailsSubmitButton.state = model.buttonState
        }

    private var validators = mutableListOf(
        EmailValidator(),
        CountryValidator(),
        StateValidator(),
        PhoneCountryCodeValidator(),
        MobileNumberValidator(),
        CityValidator(),
        PostcodeValidator(),
        AddressLineValidator()
    )

    private val phoneNumberFields =
        arrayListOf(BillingDetailsFieldType.PHONE_COUNTRY_CODE.name, BillingDetailsFieldType.MOBILE_NUMBER.name)

    private val countries: Array<CountryInfo>
        get() = CountryInfo.list(context)

    private val validationResultsCache = mutableMapOf<BillingDetailsFieldType, Boolean>()

    private val phoneCountryCodeFormatter: PhoneCountryCodeTextWatcher by lazy {
        PhoneCountryCodeTextWatcher()
    }

    private var mobileNumberFormatter: PhoneNumberFormattingTextWatcher? = null
    private var selectedState: State? = null
    private var selectedCountry: CountryInfo? = null
        set(value) {
            field = value

            val dialCode = value?.dialCode ?: ""
            val country = Country.values().firstOrNull { it.name == selectedCountry?.alpha2Code } ?: Country.OTHER

            textInputLayoutForType(BillingDetailsFieldType.PHONE_COUNTRY_CODE)?.let {
                it.editText?.setText(dialCode)
            }

            validatorInstance<PostcodeValidator>()?.let {
                it.country = country
            }

            validatorInstance<StateValidator>()?.let {
                it.country = country
            }

            setupStateSpinner(country)
            updateSubmitButtonState()
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupFieldsContent()
        setupPhoneCountryCodeFormatter()
        setupMobileNumberFormatter()
        setupCountrySpinner()
        setupStateSpinner(Country.OTHER)
    }

    private fun setupCountrySpinner() = countryTextInputEditText.apply {
        val adapter = ArrayAdapter(context, R.layout.country_select_dialog_item, countries)
        setAdapter(adapter)
        setOnItemClickListener { parent, _, index, _ ->
            selectedCountry = parent.getItemAtPosition(index) as CountryInfo
        }
    }

    private fun setupStateSpinner(country: Country) {
        var states = emptyList<State>()
        var hint = R.string.empty
        when (country) {
            Country.US -> {
                states = usStates
                hint = R.string.us_state_hint
            }
            Country.CA -> {
                states = canadaProvincesAndTerritories
                hint = R.string.ca_province_hint
            }
            else -> {
                validationResultsCache[BillingDetailsFieldType.STATE] = true
            }
        }
        val hasStates = states.isNotEmpty()
        if (hasStates) {
            validationResultsCache[BillingDetailsFieldType.STATE] = false
            stateTextInputEditText.apply {
                setHint(hint)
                setAdapter(ArrayAdapter(context, R.layout.country_select_dialog_item, states))
                setOnItemClickListener { parent, _, position, _ ->
                    selectedState = parent.getItemAtPosition(position) as State
                }
            }
        }
        selectedState = null
        stateTextInputEditText.setText("")
        stateTextInputLayout.isVisible = hasStates
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mobileNumberFormatter = PhoneNumberFormattingTextWatcher(
                    countries.find {
                        it.name == editTextForType(BillingDetailsFieldType.COUNTRY).text.toString()
                    }?.alpha2Code ?: ""
                )
                addTextChangedListener(mobileNumberFormatter)
            }
        }

    private fun setupFieldsContent() {
        val scrollView = billingDetailsScrollView

        setAddAddressButtonClickListener()
        billingDetailsBackButton.setOnClickListener { onBillingDetailsBackButtonClickListener?.invoke() }
        billingDetailsSubmitButton.setOnClickListener {
            dismissKeyboard()
            onBillingDetailsSubmitButtonClickListener?.invoke()
        }

        BillingDetailsFieldType.values().forEach { type ->
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

    private fun setAddAddressButtonClickListener() {
        addAddressLineButton.text = context.getString(R.string.add_address_line, 2)
        addAddressLineButton.setOnClickListener {
            if (addressLine2TextInputLayout.visibility == VISIBLE) {
                addressLine3TextInputLayout.visibility = VISIBLE
                addAddressLineButton.visibility = GONE
            } else {
                addressLine2TextInputLayout.visibility = VISIBLE
                addAddressLineButton.text = context.getString(R.string.add_address_line, 3)
            }
        }
    }

    private fun editTextForType(type: BillingDetailsFieldType): EditText = when (type) {
        BillingDetailsFieldType.COUNTRY -> countryTextInputEditText
        BillingDetailsFieldType.STATE -> stateTextInputEditText
        BillingDetailsFieldType.POST_CODE -> postalCodeTextInputEditText
        BillingDetailsFieldType.EMAIL -> emailTextInputEditText
        BillingDetailsFieldType.PHONE_COUNTRY_CODE -> phoneCountryCodeTextInputEditText
        BillingDetailsFieldType.MOBILE_NUMBER -> mobileNumberTextInputEditText
        BillingDetailsFieldType.ADDRESS_LINE_1 -> addressLine1TextInputEditText
        BillingDetailsFieldType.ADDRESS_LINE_2 -> addressLine2TextInputEditText
        BillingDetailsFieldType.ADDRESS_LINE_3 -> addressLine3TextInputEditText
        BillingDetailsFieldType.CITY -> cityTextInputEditText
    }

    private fun textDidChange(type: BillingDetailsFieldType, value: String, event: FormFieldEvent) {
        val validationResults = validators.mapNotNull {
            if (it.fieldType == type.name) {
                if (phoneNumberFields.contains(it.fieldType)) {
                    val code = valueOfEditTextWithType(BillingDetailsFieldType.PHONE_COUNTRY_CODE)
                    val number = valueOfEditTextWithType(BillingDetailsFieldType.MOBILE_NUMBER)
                    when {
                        // in case country code is empty, don't apply the phone validation
                        code.isEmpty() && number.isEmpty() -> return@mapNotNull null
                        code.isEmpty() && number.isNotEmpty() -> return@mapNotNull ValidationResult(
                            false,
                            R.string.invalid_phone_country_code
                        )
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
        val validationResults = BillingDetailsFieldType.values().map {
            validationResultsCache[it] ?: false
        }

        var isFormValid = false
        if (validationResults.isNotEmpty()) {
            isFormValid = validationResults.reduce { acc, b -> acc && b }
        }

        onValidationPassed(isFormValid)
    }

    private fun onValidationPassed(isFormValid: Boolean) {
        val inputModel = BillingDetailsInputModel(
            countryCode = selectedCountry?.numericCode ?: "",
            state = selectedState?.isoCode ?: "",
            postalCode = valueOfEditTextWithType(BillingDetailsFieldType.POST_CODE),
            email = valueOfEditTextWithType(BillingDetailsFieldType.EMAIL),
            addressLine1 = valueOfEditTextWithType(BillingDetailsFieldType.ADDRESS_LINE_1),
            addressLine2 = valueOfEditTextWithType(BillingDetailsFieldType.ADDRESS_LINE_2),
            addressLine3 = valueOfEditTextWithType(BillingDetailsFieldType.ADDRESS_LINE_3),
            phoneCountryCode = valueOfEditTextWithType(BillingDetailsFieldType.PHONE_COUNTRY_CODE),
            mobileNumber = valueOfEditTextWithType(BillingDetailsFieldType.MOBILE_NUMBER),
            city = valueOfEditTextWithType(BillingDetailsFieldType.CITY)
        )

        onFormValidationStatusListener?.invoke(inputModel, isFormValid)
    }

    private inline fun <reified V> validatorInstance(): V? {
        return validators.firstOrNull { it is V } as V?
    }
}
