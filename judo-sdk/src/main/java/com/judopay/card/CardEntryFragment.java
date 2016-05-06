package com.judopay.card;


import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.R;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.CardNetwork;
import com.judopay.model.Country;
import com.judopay.validation.CardNumberValidator;
import com.judopay.validation.CountryAndPostcodeValidator;
import com.judopay.validation.ExpiryDateValidator;
import com.judopay.validation.IssueNumberValidator;
import com.judopay.validation.SecurityCodeValidator;
import com.judopay.validation.StartDateValidator;
import com.judopay.validation.Validation;
import com.judopay.validation.ValidationAutoAdvanceManager;
import com.judopay.validation.ValidationManager;
import com.judopay.validation.Validator;
import com.judopay.view.CardNumberEntryView;
import com.judopay.view.CountrySpinnerAdapter;
import com.judopay.view.ExpiryDateEntryView;
import com.judopay.view.IssueNumberEntryView;
import com.judopay.view.PostcodeEntryView;
import com.judopay.view.SecurityCodeEntryView;
import com.judopay.view.SimpleTextWatcher;
import com.judopay.view.SingleClickOnClickListener;
import com.judopay.view.StartDateEntryView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.observables.ConnectableObservable;

import static com.judopay.Judo.isAvsEnabled;
import static com.judopay.arch.TextUtil.isEmpty;

/**
 * A Fragment that allows for card details to be entered by the user, with validation checks
 * on input data.
 * Configuration options can be provided by passing a {@link JudoOptions} instance in the fragment
 * arguments, identified using the {@link Judo#JUDO_OPTIONS} as a key, e.g.
 * <code>
 * CardEntryFragment fragment = new CardEntryFragment();
 * Bundle args = new Bundle();
 * args.putParcelable(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
 * .setJudoId("123456")
 * .setAmount("1.99")
 * .setCurrency(Currency.USD)
 * .setButtonLabel("Perform payment")
 * .setSecureServerMessageShown(true)
 * .build())
 * fragment.setArguments(args);
 * </code>
 */
public final class CardEntryFragment extends AbstractCardEntryFragment {

    private Button paymentButton;
    private Spinner countrySpinner;
    private SecurityCodeEntryView securityCodeEntryView;
    private CardNumberEntryView cardNumberEntryView;

    private View startDateAndIssueNumberContainer;
    private View countryAndPostcodeContainer;
    private IssueNumberEntryView issueNumberEntryView;
    private PostcodeEntryView postcodeEntryView;
    private StartDateEntryView startDateEntryView;
    private ExpiryDateEntryView expiryDateEntryView;
    private View secureServerText;

    private ValidationManager validationManager;
    private ValidationManager avsValidationManager;

    private StartDateValidator startDateValidator;
    private IssueNumberValidator issueNumberValidator;
    private SecurityCodeValidator securityCodeValidator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_entry, container, false);

        paymentButton = (Button) view.findViewById(R.id.payment_button);

        securityCodeEntryView = (SecurityCodeEntryView) view.findViewById(R.id.security_code_entry_view);
        cardNumberEntryView = (CardNumberEntryView) view.findViewById(R.id.card_number_entry_view);
        expiryDateEntryView = (ExpiryDateEntryView) view.findViewById(R.id.expiry_date_entry_view);

        postcodeEntryView = (PostcodeEntryView) view.findViewById(R.id.postcode_entry_view);

        countrySpinner = (Spinner) view.findViewById(R.id.country_spinner);
        startDateEntryView = (StartDateEntryView) view.findViewById(R.id.start_date_entry_view);

        issueNumberEntryView = (IssueNumberEntryView) view.findViewById(R.id.issue_number_entry_view);

        startDateAndIssueNumberContainer = view.findViewById(R.id.start_date_issue_number_container);
        countryAndPostcodeContainer = view.findViewById(R.id.country_postcode_container);
        secureServerText = view.findViewById(R.id.secure_server_text);

        return view;
    }

    @Override
    protected void onInitialize(final JudoOptions options) {
        if (options.getButtonLabel() != null) {
            paymentButton.setText(options.getButtonLabel());
        }

        if (options.getCardScanningIntent() != null) {
            cardNumberEntryView.setScanCardListener(new CardNumberEntryView.ScanCardButtonListener() {
                @Override
                public void onClick() {
                    PendingIntent cardScanningIntent = options.getCardScanningIntent();
                    if (cardScanningIntent != null) {
                        IntentSender intentSender = cardScanningIntent.getIntentSender();
                        try {
                            getActivity().startIntentSenderForResult(intentSender, Judo.CARD_SCANNING_REQUEST, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException ignore) {
                        }
                    }
                }
            });
        }

        if (options.getCardNumber() != null) {
            int cardType = CardNetwork.fromCardNumber(options.getCardNumber());
            cardNumberEntryView.setCardType(cardType, false);
            cardNumberEntryView.setText(options.getCardNumber());
            expiryDateEntryView.requestFocus();
        }

        if (options.getExpiryYear() != null && options.getExpiryMonth() != null) {
            expiryDateEntryView.setText(getString(R.string.expiry_date_format, options.getExpiryMonth(), options.getExpiryYear()));
            securityCodeEntryView.requestFocus();
        }

        if (options.isSecureServerMessageShown()) {
            secureServerText.setVisibility(View.VISIBLE);
        } else {
            secureServerText.setVisibility(View.GONE);
        }

        initializeCountry();
        initializeValidators();
        initializePayButton();
    }

    @Override
    public void setCard(Card card) {
        if(!isEmpty(card.getCardNumber())) {
            int cardType = CardNetwork.fromCardNumber(card.getCardNumber());

            cardNumberEntryView.setCardType(cardType, false);
            cardNumberEntryView.setText(card.getCardNumber());
            cardNumberEntryView.setScanCardListener(null);
            expiryDateEntryView.requestFocus();

            if(!isEmpty(card.getExpiryDate())) {
                expiryDateEntryView.setText(card.getExpiryDate());
                securityCodeEntryView.getEditText().requestFocus();
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void initializeValidators() {
        List<Validator> validators = new ArrayList<>();
        List<Pair<Validator, View>> validatorViews = new ArrayList<>();

        cardNumberEntryView.getEditText().addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onTextChanged(CharSequence text) {
                int cardType = CardNetwork.fromCardNumber(text.toString());

                cardNumberEntryView.setCardType(cardType, true);
                securityCodeEntryView.setCardType(cardType, true);
                securityCodeValidator.setCardType(cardType);

                EditText securityCodeTextInputEditText = securityCodeEntryView.getEditText();
                if (securityCodeTextInputEditText != null) {
                    securityCodeTextInputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(CardNetwork.securityCodeLength(cardType))});
                }

                if (Judo.isMaestroEnabled() && cardType == CardNetwork.MAESTRO) {
                    validationManager.addValidator(issueNumberValidator);
                    validationManager.addValidator(startDateValidator);

                    if (isAvsEnabled()) {
                        avsValidationManager.addValidator(startDateValidator);
                        avsValidationManager.addValidator(issueNumberValidator);
                    }
                    startDateAndIssueNumberContainer.setVisibility(View.VISIBLE);
                } else {
                    validationManager.removeValidator(startDateValidator);
                    validationManager.removeValidator(issueNumberValidator);

                    if (isAvsEnabled()) {
                        avsValidationManager.removeValidator(startDateValidator);
                        avsValidationManager.removeValidator(issueNumberValidator);
                    }
                    startDateAndIssueNumberContainer.setVisibility(View.GONE);
                }
            }
        });

        CardNumberValidator cardNumberValidator = new CardNumberValidator(cardNumberEntryView.getEditText(), Judo.isMaestroEnabled(), Judo.isAmexEnabled());
        cardNumberValidator.onValidate()
                .subscribe(new Action1<Validation>() {
                    @Override
                    public void call(Validation validation) {
                        cardNumberEntryView.setValidation(validation);
                    }
                });

        validators.add(cardNumberValidator);
        validatorViews.add(new Pair<Validator, View>(cardNumberValidator, cardNumberEntryView.getEditText()));

        startDateValidator = getStartDateValidator();
        validatorViews.add(new Pair<Validator, View>(startDateValidator, startDateEntryView.getEditText()));

        issueNumberValidator = getIssueNumberValidator();
        validatorViews.add(new Pair<Validator, View>(issueNumberValidator, issueNumberEntryView.getEditText()));

        ExpiryDateValidator expiryDateValidator = getExpiryDateValidator();
        validators.add(expiryDateValidator);
        validatorViews.add(new Pair<Validator, View>(expiryDateValidator, expiryDateEntryView.getEditText()));

        securityCodeValidator = new SecurityCodeValidator(securityCodeEntryView.getEditText());
        validators.add(securityCodeValidator);
        validatorViews.add(new Pair<Validator, View>(securityCodeValidator, securityCodeEntryView.getEditText()));

        validationManager = new ValidationManager(validators, this);

        if (isAvsEnabled()) {
            ArrayList<Validator> avsValidators = new ArrayList<>();
            avsValidators.add(cardNumberValidator);
            avsValidators.add(expiryDateValidator);
            avsValidators.add(securityCodeValidator);

            avsValidationManager = new ValidationManager(avsValidators, new ValidationManager.OnChangeListener() {
                @Override
                public void onValidate(boolean valid) {
                    countryAndPostcodeContainer.setVisibility(valid ? View.VISIBLE : View.GONE);
                }
            });
            initializeAvsValidators(validatorViews);
        }

        new ValidationAutoAdvanceManager(validationManager, validatorViews);
    }

    private void initializeAvsValidators(List<Pair<Validator, View>> validatorViews) {
        CountryAndPostcodeValidator countryAndPostcodeValidator = new CountryAndPostcodeValidator(countrySpinner, postcodeEntryView.getEditText());
        ConnectableObservable<Validation> observable = countryAndPostcodeValidator.onValidate();

        observable.subscribe(new Action1<Validation>() {
            @Override
            public void call(Validation validation) {
                postcodeEntryView.setError(validation.getError(), validation.isShowError());
            }
        });

        observable.subscribe(new Action1<Validation>() {
            @Override
            public void call(Validation validation) {
                String country = (String) countrySpinner.getSelectedItem();
                postcodeEntryView.setHint(Country.postcodeName(country));

                boolean postcodeNumeric = Country.UNITED_STATES.equals(country);

                EditText editText = postcodeEntryView.getEditText();
                if (editText != null) {
                    editText.setEnabled(!Country.OTHER.equals(country));

                    if (postcodeNumeric && editText.getInputType() != InputType.TYPE_CLASS_NUMBER) {
                        editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        int alphanumericInputTypes = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
                        if (!postcodeNumeric && editText.getInputType() != alphanumericInputTypes) {
                            editText.setRawInputType(alphanumericInputTypes);
                        }
                    }
                    // prevent text suggestions in keyboard
                    editText.setPrivateImeOptions("nm");
                }
            }
        });

        validationManager.addValidator(countryAndPostcodeValidator, observable);
        validatorViews.add(new Pair<Validator, View>(countryAndPostcodeValidator, postcodeEntryView.getEditText()));

        observable.connect();
    }

    private ExpiryDateValidator getExpiryDateValidator() {
        ExpiryDateValidator expiryDateValidator = new ExpiryDateValidator(expiryDateEntryView.getEditText());
        expiryDateValidator.onValidate()
                .subscribe(new Action1<Validation>() {
                    @Override
                    public void call(Validation validation) {
                        expiryDateEntryView.setValidation(validation);
                    }
                });
        return expiryDateValidator;
    }

    private IssueNumberValidator getIssueNumberValidator() {
        return new IssueNumberValidator(issueNumberEntryView.getEditText());
    }

    private StartDateValidator getStartDateValidator() {
        StartDateValidator startDateValidator = new StartDateValidator(startDateEntryView.getEditText());
        startDateValidator.onValidate()
                .subscribe(new Action1<Validation>() {
                    @Override
                    public void call(Validation validation) {
                        startDateEntryView.setValidation(validation);
                    }
                });
        return startDateValidator;
    }

    private void initializePayButton() {
        paymentButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                hideKeyboard();
                submitForm();
            }
        });
    }

    private void initializeCountry() {
        countrySpinner.setAdapter(new CountrySpinnerAdapter(getActivity(), Country.avsCountries()));
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String country = (String) countrySpinner.getSelectedItem();
                postcodeEntryView.setHint(Country.postcodeName(country));
                boolean postcodeNumeric = Country.UNITED_STATES.equals(country);
                postcodeEntryView.setNumericInput(postcodeNumeric);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void submitForm() {
        Card.Builder cardBuilder = new Card.Builder()
                .setCardNumber(cardNumberEntryView.getText())
                .setExpiryDate(expiryDateEntryView.getText())
                .setSecurityCode(securityCodeEntryView.getText());

        Address.Builder addressBuilder = new Address.Builder()
                .setPostCode(postcodeEntryView.getText());

        if (isAvsEnabled()) {
            addressBuilder.setCountryCode(Country.codeFromCountry((String) countrySpinner.getSelectedItem()));
        }

        cardBuilder.setCardAddress(addressBuilder.build());

        if (cardNumberEntryView.getCardType() == CardNetwork.MAESTRO) {
            cardBuilder.setIssueNumber(issueNumberEntryView.getText())
                    .setStartDate(startDateEntryView.getText());
        }

        if (cardEntryListener != null) {
            cardEntryListener.onSubmit(cardBuilder.build());
        }
    }

    public static CardEntryFragment newInstance(JudoOptions judoOptions, CardEntryListener listener) {
        CardEntryFragment cardEntryFragment = new CardEntryFragment();
        cardEntryFragment.setCardEntryListener(listener);

        Bundle arguments = new Bundle();
        arguments.putParcelable(Judo.JUDO_OPTIONS, judoOptions);
        cardEntryFragment.setArguments(arguments);

        return cardEntryFragment;
    }

    @Override
    public void onValidate(boolean valid) {
        paymentButton.setVisibility(valid ? View.VISIBLE : View.GONE);
    }
}