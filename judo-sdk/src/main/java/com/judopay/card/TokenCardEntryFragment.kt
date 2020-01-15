package com.judopay.card

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.judopay.Judo
import com.judopay.R
import com.judopay.arch.ThemeUtil
import com.judopay.model.Address
import com.judopay.model.Card
import com.judopay.model.CardToken
import com.judopay.model.Country
import com.judopay.validation.CountryAndPostcodeValidator
import com.judopay.validation.SecurityCodeValidator
import com.judopay.validation.ValidationAutoAdvanceManager
import com.judopay.validation.ValidationManager
import com.judopay.validation.Validator
import com.judopay.view.CountrySpinnerAdapter
import com.judopay.view.SingleClickOnClickListener
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_card_entry.button
import kotlinx.android.synthetic.main.fragment_card_entry.cancelButton
import kotlinx.android.synthetic.main.fragment_card_entry.secureServerText
import kotlinx.android.synthetic.main.view_card_entry_form.cardNumberEntryView
import kotlinx.android.synthetic.main.view_card_entry_form.countryPostcodeContainer
import kotlinx.android.synthetic.main.view_card_entry_form.countrySpinner
import kotlinx.android.synthetic.main.view_card_entry_form.expiryDateEntryView
import kotlinx.android.synthetic.main.view_card_entry_form.postcodeEntryView
import kotlinx.android.synthetic.main.view_card_entry_form.securityCodeEntryView
import java.util.*

class TokenCardEntryFragment : AbstractCardEntryFragment() {
    private var validationManager: ValidationManager? = null
    private val disposables = CompositeDisposable()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_card_entry, container, false)


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            val bottomSheet =
                dialog.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet)
                .setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return dialog
    }

    override fun onInitialize(savedInstanceState: Bundle?, judo: Judo) {
        val cardToken = judo.cardToken
            ?: throw IllegalArgumentException("CardToken is required in Judo for TokenCardEntryFragment")
        if (activity != null) {
            val secureServerMessageShown =
                ThemeUtil.getBooleanAttr(activity, R.attr.secureServerMessageShown)
            if (secureServerMessageShown) {
                secureServerText.visibility = View.VISIBLE
            } else {
                secureServerText.visibility = View.GONE
            }
        }
        cancelButton.setOnClickListener { onCancel(requireDialog()) }
        initializeInputs(cardToken, judo)
        initializePayButton(judo)
        initializeCountry()
        initializeValidators(cardToken, judo)
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun initializeCountry() {
        countrySpinner.adapter = CountrySpinnerAdapter(activity, Country.values())
    }

    private fun initializeInputs(cardToken: CardToken, options: Judo) {
        cardNumberEntryView.setCardType(cardToken.type, false)
        securityCodeEntryView.setCardType(cardToken.type, false)
        expiryDateEntryView.setExpiryDate(options.cardToken.formattedEndDate)
        expiryDateEntryView.isEnabled = false
        cardNumberEntryView.setTokenCard(options.cardToken)
    }

    private fun initializeValidators(cardToken: CardToken, judo: Judo) {
        val validators: MutableList<Validator> = ArrayList()
        val validatorViews: MutableList<Pair<Validator, View?>> = ArrayList()
        val securityCodeValidator = SecurityCodeValidator(securityCodeEntryView.editText)
        securityCodeValidator.setCardType(cardToken.type)
        disposables.add(securityCodeValidator.onValidate()
            .subscribe {
                if (judo.isAvsEnabled) {
                    countryPostcodeContainer.visibility =
                        if (it.isValid) View.VISIBLE else View.GONE
                }
            }
        )
        validators.add(securityCodeValidator)
        validatorViews.add(Pair(securityCodeValidator, securityCodeEntryView.editText))
        validationManager = ValidationManager(validators, this)
        if (judo.isAvsEnabled) {
            initializeAvsValidators(validatorViews)
        }
        ValidationAutoAdvanceManager.bind(validationManager, validatorViews)
    }

    private fun initializeAvsValidators(validatorViews: MutableList<Pair<Validator, View?>>) {
        val countryAndPostcodeValidator =
            CountryAndPostcodeValidator(countrySpinner, postcodeEntryView.editText)
        val observable = countryAndPostcodeValidator.onValidate()
        disposables.add(observable.subscribe {
            postcodeEntryView.setValidation(it)
        })
        disposables.add(observable.subscribe {
            val country = countrySpinner.selectedItem as Country
            postcodeEntryView.setCountry(country)
        })
        validationManager?.addValidator(countryAndPostcodeValidator, observable)
        validatorViews.add(Pair(countryAndPostcodeValidator, postcodeEntryView.editText))
        observable.connect()
    }

    override fun onValidate(valid: Boolean) {
        button.getButton().isEnabled = valid
    }

    private fun initializePayButton(judo: Judo) {
        button.getButton().setOnClickListener(object : SingleClickOnClickListener() {
            override fun doClick() {
                if (activity != null) {
                    val view = requireActivity().currentFocus
                    view?.clearFocus()
                    button.startLoading()
                    hideKeyboard()
                    submitForm(judo)
                }
            }
        })
    }

    private fun submitForm(judo: Judo) {
        val cardBuilder = Card.Builder()
            .setCardNumber(cardNumberEntryView.getText())
            .setExpiryDate(expiryDateEntryView.getText())
            .setSecurityCode(securityCodeEntryView.getText())
        if (judo.isAvsEnabled) {
            cardBuilder.setAddress(
                Address.Builder()
                    .setPostCode(postcodeEntryView.getText())
                    .setCountryCode((countrySpinner.selectedItem as Country).countryCode)
                    .build()
            )
        }
        cardEntryListener.onSubmit(cardBuilder.build())
    }

    companion object {
        fun newInstance(judo: Judo, listener: CardEntryListener): TokenCardEntryFragment {
            val cardEntryFragment = TokenCardEntryFragment()
            cardEntryFragment.cardEntryListener = listener
            val arguments = Bundle()
            arguments.putParcelable(Judo.JUDO_OPTIONS, judo)
            cardEntryFragment.arguments = arguments
            return cardEntryFragment
        }
    }
}