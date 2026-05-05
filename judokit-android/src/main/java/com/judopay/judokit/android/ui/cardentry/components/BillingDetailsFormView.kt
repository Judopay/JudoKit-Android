package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomappbar.BottomAppBar
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.BillingDetailsFormViewBinding
import com.judopay.judokit.android.dismissKeyboard
import com.judopay.judokit.android.parentOfType
import com.judopay.judokit.android.smoothScrollToView
import com.judopay.judokit.android.ui.cardentry.formatting.PhoneCountryCodeTextWatcher
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.model.adminDivisionConfig
import com.judopay.judokit.android.ui.cardentry.model.fieldHintResId

interface BillingDetailsFormListener {
    fun onFieldChanged(
        type: BillingDetailsFieldType,
        value: String,
        event: FormFieldEvent,
    )

    fun onSubmit()

    fun onBack()
}

class BillingDetailsFormView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : FrameLayout(context, attrs, defStyle),
        CardEntryViewAnimator.OnViewWillAppearListener {
        val binding = BillingDetailsFormViewBinding.inflate(LayoutInflater.from(context), this, true)

        val scrollView: NestedScrollView get() = binding.billingDetailsScrollView
        val bottomAppBar: BottomAppBar get() = binding.billingDetailsBottomAppBar
        val containerLayout: ConstraintLayout get() = binding.billingDetailsContainerLayout

        internal var listener: BillingDetailsFormListener? = null

        internal var model = BillingDetailsInputModel()
            set(value) {
                field = value
                binding.billingDetailsSubmitButton.state = model.submitButtonState
                binding.billingDetailsBackButton.state = model.backButtonState
                binding.administrativeDivisionTextInputLayout.isVisible = model.adminDivisionRequired
                renderErrors()
            }

        private val countries = Country.list(context)

        private val phoneCountryCodeFormatter: PhoneCountryCodeTextWatcher by lazy {
            PhoneCountryCodeTextWatcher()
        }

        private var mobileNumberFormatter: PhoneNumberFormattingTextWatcher? = null
        private var selectedCountry: Country? = null
            set(value) {
                field = value
                val dialCode = value?.dialCode ?: ""
                editTextForType(BillingDetailsFieldType.PHONE_COUNTRY_CODE).setText(dialCode)
                setupStateSpinner(selectedCountry)
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
            }
        }

        private fun setupCountrySpinner() =
            binding.countryTextInputEditText.apply {
                val adapter = ArrayAdapter(context, R.layout.country_select_dialog_item, countries)
                setAdapter(adapter)
                setOnItemClickListener { parent, _, index, _ ->
                    val selected = parent.getItemAtPosition(index) as Country
                    if (selectedCountry != selected) {
                        selectedCountry = selected
                    }
                }
            }

        private fun setupStateSpinner(country: Country? = null) {
            val config = country?.adminDivisionConfig()
            if (config != null) {
                binding.administrativeDivisionTextInputEditText.apply {
                    setHint(config.hint)
                    setAdapter(DiacriticInsensitiveAdapter(context, config.divisions))
                }
            }
            binding.administrativeDivisionTextInputEditText.setText("")
            binding.administrativeDivisionTextInputLayout.isVisible = config != null
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
                        countries
                            .find {
                                it.name == editTextForType(BillingDetailsFieldType.COUNTRY).text.toString()
                            }?.alpha2Code ?: "",
                    )
                addTextChangedListener(mobileNumberFormatter)
            }

        private fun setupFieldsContent() {
            val scrollView = binding.billingDetailsScrollView

            setAddAddressButtonClickListener()
            binding.billingDetailsBackButton.setOnClickListener { listener?.onBack() }
            binding.billingDetailsSubmitButton.setOnClickListener {
                dismissKeyboard()
                listener?.onSubmit()
            }

            BillingDetailsFieldType.entries.forEach { type ->
                editTextForType(type).apply {
                    setHint(type.fieldHintResId)

                    if (type == BillingDetailsFieldType.MOBILE_NUMBER) {
                        setOnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                setupMobileNumberFormatter()
                                scrollView.smoothScrollToView(editTextForType(type))
                            } else {
                                val text = valueOfEditTextWithType(type)
                                listener?.onFieldChanged(type, text, FormFieldEvent.FOCUS_CHANGED)
                            }
                        }
                    } else {
                        setOnFocusChangeListener { _, hasFocus ->
                            if (!hasFocus) {
                                val text = valueOfEditTextWithType(type)
                                listener?.onFieldChanged(type, text, FormFieldEvent.FOCUS_CHANGED)
                            } else {
                                scrollView.smoothScrollToView(editTextForType(type))
                            }
                        }
                    }

                    addTextChangedListener {
                        val text = it.toString()
                        if (type == BillingDetailsFieldType.COUNTRY) {
                            val typedCountry =
                                countries.firstOrNull { country ->
                                    country.name.equals(text, ignoreCase = true)
                                }
                            if (selectedCountry != typedCountry) {
                                selectedCountry = typedCountry
                            }
                        }
                        listener?.onFieldChanged(type, text, FormFieldEvent.TEXT_CHANGED)
                    }
                }
            }
        }

        private fun renderErrors() {
            renderFieldErrors(model.fieldErrors, context, ::textInputLayoutForType)
        }

        @Suppress("MagicNumber")
        private fun setAddAddressButtonClickListener() {
            binding.addAddressLineButtonText.text = context.getString(R.string.jp_add_address_line, 2)
            binding.addAddressLineButton.setOnClickListener {
                if (binding.addressLine2TextInputLayout.isVisible) {
                    binding.addressLine3TextInputLayout.visibility = VISIBLE
                    binding.addAddressLineButton.visibility = GONE
                } else {
                    binding.addressLine2TextInputLayout.visibility = VISIBLE
                    binding.addAddressLineButtonText.text = context.getString(R.string.jp_add_address_line, 3)
                }
            }
        }

        private fun editTextForType(type: BillingDetailsFieldType): EditText =
            when (type) {
                BillingDetailsFieldType.COUNTRY -> binding.countryTextInputEditText
                BillingDetailsFieldType.ADMINISTRATIVE_DIVISION -> binding.administrativeDivisionTextInputEditText
                BillingDetailsFieldType.POST_CODE -> binding.postalCodeTextInputEditText
                BillingDetailsFieldType.EMAIL -> binding.emailTextInputEditText
                BillingDetailsFieldType.PHONE_COUNTRY_CODE -> binding.phoneCountryCodeTextInputEditText
                BillingDetailsFieldType.MOBILE_NUMBER -> binding.mobileNumberTextInputEditText
                BillingDetailsFieldType.ADDRESS_LINE_1 -> binding.addressLine1TextInputEditText
                BillingDetailsFieldType.ADDRESS_LINE_2 -> binding.addressLine2TextInputEditText
                BillingDetailsFieldType.ADDRESS_LINE_3 -> binding.addressLine3TextInputEditText
                BillingDetailsFieldType.CITY -> binding.cityTextInputEditText
            }

        private fun textInputLayoutForType(type: BillingDetailsFieldType): JudoEditTextInputLayout? {
            val editText = editTextForType(type)
            return editText.parentOfType(JudoEditTextInputLayout::class.java)
        }

        private fun valueOfEditTextWithType(type: BillingDetailsFieldType): String {
            val editText = editTextForType(type)
            return editText.text.toString()
        }
    }
