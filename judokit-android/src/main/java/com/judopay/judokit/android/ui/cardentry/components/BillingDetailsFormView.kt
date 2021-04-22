package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.judopay.judokit.android.R
import com.judopay.judokit.android.inflate
import com.judopay.judokit.android.parentOfType
import com.judopay.judokit.android.ui.cardentry.formatting.PhoneCountryCodeTextWatcher
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.model.fieldHintResId
import com.judopay.judokit.android.ui.cardentry.model.valueOfBillingDetailsFieldWithType
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.AddressLineValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.BillingDetailsPostCodeValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.CityValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.EmailValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.MobileNumberValidator
import com.judopay.judokit.android.ui.cardentry.validation.billingdetails.PhoneCountryCodeValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CountryValidator
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
        PhoneCountryCodeValidator(),
        MobileNumberValidator(),
        CityValidator(),
        BillingDetailsPostCodeValidator(),
        AddressLineValidator()
    )

    private val countries: Array<CountryInfo>
        get() = CountryInfo.list(context)

    private val validationResultsCache = mutableMapOf<BillingDetailsFieldType, Boolean>()

    private val phoneCountryCodeFormatter: PhoneCountryCodeTextWatcher by lazy {
        PhoneCountryCodeTextWatcher()
    }
    private var mobileNumberFormatter: PhoneNumberFormattingTextWatcher? = null
    private var selectedCountry: CountryInfo? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupFieldsContent()
        setupPhoneCountryCodeFormatter()
        setupMobileNumberFormatter()
        setupCountrySpinner()
    }

    private fun setupCountrySpinner() = countryTextInputEditText.apply {
        val adapter =
            ArrayAdapter(context, android.R.layout.select_dialog_item, countries)
        setAdapter(adapter)
        setOnItemClickListener { parent, _, index, _ ->
            selectedCountry = parent.getItemAtPosition(index) as CountryInfo
            editTextForType(BillingDetailsFieldType.PHONE_COUNTRY_CODE).setText(selectedCountry?.dialCode)
        }
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
            mobileNumberFormatter = PhoneNumberFormattingTextWatcher(
                countries.find {
                    it.name == editTextForType(BillingDetailsFieldType.COUNTRY).text.toString()
                }?.alpha2Code ?: ""
            )
            addTextChangedListener(mobileNumberFormatter)
        }

    private fun setupFieldsContent() {
        setAddAddressButtonClickListener()
        billingDetailsBackButton.setOnClickListener { onBillingDetailsBackButtonClickListener?.invoke() }
        billingDetailsSubmitButton.setOnClickListener { onBillingDetailsSubmitButtonClickListener?.invoke() }

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
                        } else {
                            val text = valueOfEditTextWithType(type)
                            textDidChange(type, text, FormFieldEvent.FOCUS_CHANGED)
                        }
                    }
                } else {
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
                it.validate(value, event)
            } else null
        }

        val result = validationResults.firstOrNull()

        validationResultsCache[type] = result?.isValid ?: true

        val layout = textInputLayoutForType(type)
        val isValidResult = result?.isValid ?: true
        val message = context.getString(result?.message ?: R.string.empty)
        val errorEnabled = value.isNotBlank() && !isValidResult && message.isNotEmpty()

//        if (event == FormFieldEvent.TEXT_CHANGED)
//            autoTab(isValidResult, type)

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

    private fun autoTab(isValidResult: Boolean, type: BillingDetailsFieldType) {
        if (isValidResult &&
            type != BillingDetailsFieldType.ADDRESS_LINE_1 &&
            type != BillingDetailsFieldType.ADDRESS_LINE_2 &&
            type != BillingDetailsFieldType.ADDRESS_LINE_3 &&
            type != BillingDetailsFieldType.COUNTRY
        ) {
            val types = BillingDetailsFieldType.values().toList()
            val nextFormFieldType = types.indexOf(type) + 1
            if (types.size > nextFormFieldType) {
                when (val field = editTextForType(types[nextFormFieldType])) {
                    is AutoCompleteTextView -> {
                        editTextForType(type).clearFocus()
                        field.showDropDown()
                        field.setOnItemClickListener { _, _, _, _ ->
                            editTextForType(BillingDetailsFieldType.POST_CODE).requestFocus()
                        }
                    }
                    else -> field.requestFocus()
                }
            }
        }
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
}
