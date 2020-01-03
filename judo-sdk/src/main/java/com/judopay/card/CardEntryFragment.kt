package com.judopay.card

import android.app.Dialog
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.judopay.Judo
import com.judopay.R
import com.judopay.arch.TextUtil
import com.judopay.arch.ThemeUtil
import com.judopay.model.Address
import com.judopay.model.Card
import com.judopay.model.CardNetwork
import com.judopay.model.Country
import com.judopay.validation.CardNumberValidator
import com.judopay.validation.CountryAndPostcodeValidator
import com.judopay.validation.ExpiryDateValidator
import com.judopay.validation.IssueNumberValidator
import com.judopay.validation.SecurityCodeValidator
import com.judopay.validation.StartDateValidator
import com.judopay.validation.Validation
import com.judopay.validation.ValidationAutoAdvanceManager
import com.judopay.validation.ValidationManager
import com.judopay.validation.ValidationManager.OnChangeListener
import com.judopay.validation.Validator
import com.judopay.view.CountrySpinnerAdapter
import com.judopay.view.SimpleTextWatcher
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_card_entry.button
import kotlinx.android.synthetic.main.fragment_card_entry.cancelButton
import kotlinx.android.synthetic.main.fragment_card_entry.scanCardButton
import kotlinx.android.synthetic.main.fragment_card_entry.secureServerText
import kotlinx.android.synthetic.main.view_card_entry_form.cardNumberEntryView
import kotlinx.android.synthetic.main.view_card_entry_form.countryPostcodeContainer
import kotlinx.android.synthetic.main.view_card_entry_form.countrySpinner
import kotlinx.android.synthetic.main.view_card_entry_form.expiryDateEntryView
import kotlinx.android.synthetic.main.view_card_entry_form.issueNumberEntryView
import kotlinx.android.synthetic.main.view_card_entry_form.postcodeEntryView
import kotlinx.android.synthetic.main.view_card_entry_form.securityCodeEntryView
import kotlinx.android.synthetic.main.view_card_entry_form.startDateEntryView
import kotlinx.android.synthetic.main.view_card_entry_form.startDateIssueNumberContainer
import java.util.*

/**
 * A Fragment that allows for card details to be entered by the user, with validation checks
 * on input data.
 * Configuration options can be provided by passing a [Judo] instance in the fragment
 * arguments, identified using the [Judo.JUDO_OPTIONS] as a key, e.g.
 * `
 * CardEntryFragment fragment = new CardEntryFragment();
 * Bundle args = new Bundle();
 * args.putParcelable(Judo.JUDO_OPTIONS, new Judo.Builder()
 * .setJudoId("123456")
 * .setAmount("1.99")
 * .setCurrency(Currency.USD)
 * .setSecureServerMessageShown(true)
 * .build())
 * fragment.setArguments(args);
` *
 */
class CardEntryFragment : AbstractCardEntryFragment() {
    private lateinit var validationManager: ValidationManager
    private lateinit var avsValidationManager: ValidationManager
    private lateinit var startDateValidator: StartDateValidator
    private lateinit var issueNumberValidator: IssueNumberValidator
    private lateinit var securityCodeValidator: SecurityCodeValidator
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_card_entry, container, false);

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return dialog
    }

    override fun onInitialize(savedInstanceState: Bundle?, judo: Judo) {
        if (judo.cardScanningIntent != null) {
            scanCardButton.setOnClickListener {
                val cardScanningIntent = judo.cardScanningIntent
                val intentSender = cardScanningIntent.intentSender
                try {
                    requireActivity().startIntentSenderForResult(
                        intentSender,
                        Judo.CARD_SCANNING_REQUEST,
                        null,
                        0,
                        0,
                        0
                    )
                } catch (ignore: SendIntentException) {
                }
            }
        }
        cancelButton.setOnClickListener {
            onCancel(requireDialog())
        }
        if (judo.cardNumber != null) {
            val cardType = CardNetwork.fromCardNumber(judo.cardNumber)
            cardNumberEntryView.apply {
                setCardType(cardType, true)
                setText(judo.cardNumber)
            }
            requestFocus(expiryDateEntryView)
        } else {
            requestFocus(cardNumberEntryView)
        }
        if (judo.expiryYear != null && judo.expiryMonth != null) {
            expiryDateEntryView.setText(
                getString(
                    R.string.expiry_date_format,
                    judo.expiryMonth,
                    judo.expiryYear
                )
            )
            requestFocus(securityCodeEntryView)
        }
        val secureServerMessageShown =
            ThemeUtil.getBooleanAttr(requireActivity(), R.attr.secureServerMessageShown)
        if (secureServerMessageShown) {
            secureServerText.visibility = View.VISIBLE
        } else {
            secureServerText.visibility = View.GONE
        }
        initializeCountry()
        initializeValidators(judo)
        initializeButton(judo)
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun setCard(card: Card) {
        if (!TextUtil.isEmpty(card.cardNumber)) {
            val cardType = CardNetwork.fromCardNumber(card.cardNumber)
            cardNumberEntryView.apply {
                setCardType(cardType, true)
                setText(card.cardNumber)
            }
            requestFocus(expiryDateEntryView)
            if (!TextUtil.isEmpty(card.expiryDate)) {
                expiryDateEntryView.setText(card.expiryDate)
                requestFocus(securityCodeEntryView)
            }
        }
    }

    private fun initializeValidators(judo: Judo) {
        val validators: MutableList<Validator> = ArrayList()
        val validatorViews: MutableList<Pair<Validator, View>> = ArrayList()
        cardNumberEntryView.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(text: CharSequence) {
                val cardType = CardNetwork.fromCardNumber(text.toString())
                cardNumberEntryView.setCardType(cardType, true)
                securityCodeEntryView.setCardType(cardType, true)
                securityCodeValidator.setCardType(cardType)
                if (judo.isMaestroEnabled && cardType == CardNetwork.MAESTRO) {
                    validationManager.addValidator(issueNumberValidator)
                    validationManager.addValidator(startDateValidator)
                    if (judo.isAvsEnabled) {
                        avsValidationManager.addValidator(startDateValidator)
                        avsValidationManager.addValidator(issueNumberValidator)
                    }
                    startDateIssueNumberContainer.visibility = View.VISIBLE
                } else {
                    validationManager.removeValidator(startDateValidator)
                    validationManager.removeValidator(issueNumberValidator)
                    if (judo.isAvsEnabled) {
                        avsValidationManager.removeValidator(startDateValidator)
                        avsValidationManager.removeValidator(issueNumberValidator)
                    }
                    startDateIssueNumberContainer.visibility = View.GONE
                }
            }
        })
        val cardNumberValidator = CardNumberValidator(
            cardNumberEntryView.getEditText(),
            judo.isMaestroEnabled,
            judo.isAmexEnabled
        )
        disposables.add(cardNumberValidator.onValidate().subscribe { validation: Validation ->
            cardNumberEntryView.setValidation(validation)
        })
        validators.add(cardNumberValidator)
        validatorViews.add(Pair(cardNumberValidator, cardNumberEntryView!!.getEditText()))
        startDateValidator = getStartDateValidator()
        validatorViews.add(Pair(startDateValidator, startDateEntryView.editText))
        issueNumberValidator = getIssueNumberValidator()
        validatorViews.add(Pair(issueNumberValidator, issueNumberEntryView.editText))
        val expiryDateValidator = expiryDateValidator
        validators.add(expiryDateValidator)
        validatorViews.add(Pair(expiryDateValidator, expiryDateEntryView.getEditText()))
        securityCodeValidator = SecurityCodeValidator(securityCodeEntryView.getEditText())
        validators.add(securityCodeValidator)
        validatorViews.add(Pair(securityCodeValidator, securityCodeEntryView.getEditText()))
        validationManager = ValidationManager(validators, this)
        if (judo.isAvsEnabled) {
            val avsValidators = ArrayList<Validator>()
            avsValidators.add(cardNumberValidator)
            avsValidators.add(expiryDateValidator)
            avsValidators.add(securityCodeValidator)
            avsValidationManager = ValidationManager(
                avsValidators,
                OnChangeListener {
                    countryPostcodeContainer.visibility = if (it) View.VISIBLE else View.GONE
                }
            )
            initializeAvsValidators(validatorViews)
        }
        ValidationAutoAdvanceManager.bind(validationManager, validatorViews)
    }

    private fun initializeAvsValidators(validatorViews: MutableList<Pair<Validator, View>>) {
        val countryAndPostcodeValidator =
            CountryAndPostcodeValidator(countrySpinner, postcodeEntryView.getEditText())
        val observable = countryAndPostcodeValidator.onValidate()
        disposables.add(observable.subscribe {
            val country = countrySpinner.selectedItem as Country
            postcodeEntryView.setCountry(country)
            postcodeEntryView.setError(it.error, it.isShowError)
        })
        validationManager.addValidator(countryAndPostcodeValidator, observable)
        validatorViews.add(Pair(countryAndPostcodeValidator, postcodeEntryView.getEditText()))
        observable.connect()
    }

    private val expiryDateValidator: ExpiryDateValidator
        get() {
            val expiryDateValidator = ExpiryDateValidator(expiryDateEntryView.getEditText())
            disposables.add(expiryDateValidator.onValidate().subscribe {
                expiryDateEntryView.setValidation(it)
            })
            return expiryDateValidator
        }

    private fun getIssueNumberValidator(): IssueNumberValidator {
        return IssueNumberValidator(issueNumberEntryView.editText)
    }

    private fun getStartDateValidator(): StartDateValidator {
        val startDateValidator = StartDateValidator(startDateEntryView.editText)
        disposables.add(startDateValidator.onValidate().subscribe {
            startDateEntryView.setValidation(it)
        })
        return startDateValidator
    }

    private fun initializeButton(judo: Judo) {
        button.getButton().setOnClickListener {
            requireActivity().currentFocus?.clearFocus()
            button.startLoading()
            hideKeyboard()
            submitForm(judo)
        }
    }

    private fun initializeCountry() {
        countrySpinner.adapter = CountrySpinnerAdapter(activity, Country.values())
        countrySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val country = countrySpinner.selectedItem as Country
                postcodeEntryView.setCountry(country)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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
        if (cardNumberEntryView.getCardType() == CardNetwork.MAESTRO) {
            cardBuilder.setIssueNumber(issueNumberEntryView.text)
                .setStartDate(startDateEntryView.text)
        }
        cardEntryListener.onSubmit(cardBuilder.build())
    }

    override fun onValidate(valid: Boolean) {
        button.getButton().isEnabled = valid
    }

    private fun requestFocus(view: View) {
        Handler().postDelayed({
            view.requestFocus()
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(
                InputMethodManager.SHOW_IMPLICIT,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }, 1100)
    }

    companion object {
        fun newInstance(judo: Judo, listener: CardEntryListener): CardEntryFragment {
            val cardEntryFragment = CardEntryFragment()
            cardEntryFragment.cardEntryListener = listener
            val arguments = Bundle()
            arguments.putParcelable(Judo.JUDO_OPTIONS, judo)
            cardEntryFragment.arguments = arguments
            return cardEntryFragment
        }
    }
}