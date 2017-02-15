package com.judopay.card;

import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.judopay.Judo;
import com.judopay.R;
import com.judopay.arch.ThemeUtil;
import com.judopay.detection.CompletedField;
import com.judopay.detection.CompletedFieldsDetector;
import com.judopay.detection.FieldState;
import com.judopay.detection.PastedFieldsDetector;
import com.judopay.detection.TotalKeystrokesDetector;
import com.judopay.detection.UserAnalytics;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.functions.Action1;
import rx.observables.ConnectableObservable;

import static com.judopay.arch.TextUtil.isEmpty;

/**
 * A Fragment that allows for card details to be entered by the user, with validation checks
 * on input data.
 * Configuration options can be provided by passing a {@link Judo} instance in the fragment
 * arguments, identified using the {@link Judo#JUDO_OPTIONS} as a key, e.g.
 * <code>
 * CardEntryFragment fragment = new CardEntryFragment();
 * Bundle args = new Bundle();
 * args.putParcelable(Judo.JUDO_OPTIONS, new Judo.Builder()
 * .setJudoId("123456")
 * .setAmount("1.99")
 * .setCurrency(Currency.USD)
 * .setSecureServerMessageShown(true)
 * .build())
 * fragment.setArguments(args);
 * </code>
 */
public final class CardEntryFragment extends AbstractCardEntryFragment {

    private CardNumberEntryView cardNumberEntryView;
    private ExpiryDateEntryView expiryDateEntryView;
    private SecurityCodeEntryView securityCodeEntryView;

    private PostcodeEntryView postcodeEntryView;
    private View secureServerText;
    private View countryAndPostcodeContainer;
    private Spinner countrySpinner;

    private View startDateAndIssueNumberContainer;

    private IssueNumberEntryView issueNumberEntryView;
    private StartDateEntryView startDateEntryView;

    private ValidationManager validationManager;
    private ValidationManager avsValidationManager;

    private StartDateValidator startDateValidator;
    private IssueNumberValidator issueNumberValidator;
    private SecurityCodeValidator securityCodeValidator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_entry, container, false);

        submitButton = (Button) view.findViewById(R.id.button);

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
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        pastedFieldsDetector = new PastedFieldsDetector.Builder()
                .add("cardNumber", cardNumberEntryView.getEditText())
                .add("expiryDate", expiryDateEntryView.getEditText())
                .add("securityCode", securityCodeEntryView.getEditText())
                .add("startDate", startDateEntryView.getEditText())
                .add("issueNumber", issueNumberEntryView.getEditText())
                .add("postcode", postcodeEntryView.getEditText())
                .build();

        keystrokesDetector = new TotalKeystrokesDetector.Builder()
                .add("cardNumber", cardNumberEntryView.getEditText())
                .add("expiryDate", expiryDateEntryView.getEditText())
                .add("securityCode", securityCodeEntryView.getEditText())
                .add("startDate", startDateEntryView.getEditText())
                .add("issueNumber", issueNumberEntryView.getEditText())
                .add("postcode", postcodeEntryView.getEditText())
                .build();

        if (savedInstanceState != null) {
            keystrokesDetector.setTotalKeystrokes(savedInstanceState.getInt(KEY_KEYSTROKES));
            //noinspection unchecked
            ConcurrentHashMap<String, ArrayList<Long>> pastedFields = new ConcurrentHashMap<>((HashMap<String, ArrayList<Long>>) savedInstanceState.getSerializable(KEY_PASTED_FIELDS));

            pastedFieldsDetector.setPasteTimings(pastedFields);
        }
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState, final Judo judo) {
        if (judo.getCardScanningIntent() != null) {
            cardNumberEntryView.setScanCardListener(new CardNumberEntryView.ScanCardButtonListener() {
                @Override
                public void onClick() {
                    PendingIntent cardScanningIntent = judo.getCardScanningIntent();
                    if (cardScanningIntent != null) {
                        IntentSender intentSender = cardScanningIntent.getIntentSender();
                        try {
                            getActivity().startIntentSenderForResult(intentSender, Judo.CARD_SCANNING_REQUEST, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException ignore) { }
                    }
                }
            });
        }

        if (judo.getCardNumber() != null) {
            int cardType = CardNetwork.fromCardNumber(judo.getCardNumber());
            cardNumberEntryView.setCardType(cardType, false);
            cardNumberEntryView.setText(judo.getCardNumber());
            expiryDateEntryView.requestFocus();
        }

        if (judo.getExpiryYear() != null && judo.getExpiryMonth() != null) {
            expiryDateEntryView.setText(getString(R.string.expiry_date_format, judo.getExpiryMonth(), judo.getExpiryYear()));
            securityCodeEntryView.requestFocus();
        }

        boolean secureServerMessageShown = ThemeUtil.getBooleanAttr(getActivity(), R.attr.secureServerMessageShown);
        if (secureServerMessageShown) {
            secureServerText.setVisibility(View.VISIBLE);
        } else {
            secureServerText.setVisibility(View.GONE);
        }

        initializeCountry();
        initializeValidators(savedInstanceState, judo);
        initializeButton(judo);
    }

    @Override
    public void setCard(Card card) {
        if (!isEmpty(card.getCardNumber())) {
            int cardType = CardNetwork.fromCardNumber(card.getCardNumber());

            cardNumberEntryView.setCardType(cardType, false);
            cardNumberEntryView.setText(card.getCardNumber());
            cardNumberEntryView.setScanCardListener(null);
            expiryDateEntryView.requestFocus();

            if (!isEmpty(card.getExpiryDate())) {
                expiryDateEntryView.setText(card.getExpiryDate());
                securityCodeEntryView.getEditText().requestFocus();
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void initializeValidators(Bundle savedInstanceState, final Judo judo) {
        List<Validator> validators = new ArrayList<>();
        List<Pair<Validator, View>> validatorViews = new ArrayList<>();

        cardNumberEntryView.getEditText().addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onTextChanged(CharSequence text) {
                int cardType = CardNetwork.fromCardNumber(text.toString());

                cardNumberEntryView.setCardType(cardType, true);
                securityCodeEntryView.setCardType(cardType, true);
                securityCodeValidator.setCardType(cardType);

                if (judo.isMaestroEnabled() && cardType == CardNetwork.MAESTRO) {
                    validationManager.addValidator(issueNumberValidator);
                    validationManager.addValidator(startDateValidator);

                    if (judo.isAvsEnabled()) {
                        avsValidationManager.addValidator(startDateValidator);
                        avsValidationManager.addValidator(issueNumberValidator);
                    }
                    startDateAndIssueNumberContainer.setVisibility(View.VISIBLE);
                } else {
                    validationManager.removeValidator(startDateValidator);
                    validationManager.removeValidator(issueNumberValidator);

                    if (judo.isAvsEnabled()) {
                        avsValidationManager.removeValidator(startDateValidator);
                        avsValidationManager.removeValidator(issueNumberValidator);
                    }
                    startDateAndIssueNumberContainer.setVisibility(View.GONE);
                }
            }
        });

        CardNumberValidator cardNumberValidator = new CardNumberValidator(cardNumberEntryView.getEditText(), judo.isMaestroEnabled(), judo.isAmexEnabled());
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

        CompletedFieldsDetector.Builder builder = new CompletedFieldsDetector.Builder()
                .add("cardNumber", cardNumberValidator.onValidate(), cardNumberEntryView.getEditText())
                .add("expiryDate", expiryDateValidator.onValidate(), expiryDateEntryView.getEditText())
                .add("securityCode", securityCodeValidator.onValidate(), securityCodeEntryView.getEditText())
                .add("issueNumber", issueNumberValidator.onValidate(), issueNumberEntryView.getEditText())
                .add("startDate", startDateValidator.onValidate(), startDateEntryView.getEditText());

        if (judo.isAvsEnabled()) {
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
            initializeAvsValidators(validatorViews, builder);
        }

        completedFieldsDetector = builder.build();

        if (savedInstanceState != null) {
            ArrayList<CompletedField> completedFields = savedInstanceState.getParcelableArrayList(KEY_COMPLETED_FIELDS);
            completedFieldsDetector.setCompletedFields(completedFields);
            //noinspection unchecked
            completedFieldsDetector.setFieldStateMap(new ConcurrentHashMap<>((HashMap<String, FieldState>) savedInstanceState.getSerializable(KEY_FIELD_STATES)));
        }

        ValidationAutoAdvanceManager.bind(validationManager, validatorViews);
    }

    private void initializeAvsValidators(List<Pair<Validator, View>> validatorViews, CompletedFieldsDetector.Builder builder) {
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
                postcodeEntryView.setCountry(country);
            }
        });

        builder.add("postcode", observable, postcodeEntryView.getEditText());

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

    private void initializeButton(final Judo judo) {
        submitButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    view.clearFocus();
                }

                hideKeyboard();
                submitForm(judo);
            }
        });
    }

    private void initializeCountry() {
        countrySpinner.setAdapter(new CountrySpinnerAdapter(getActivity(), Country.avsCountries()));
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String country = (String) countrySpinner.getSelectedItem();
                postcodeEntryView.setCountry(country);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void submitForm(Judo judo) {
        Card.Builder cardBuilder = new Card.Builder()
                .setCardNumber(cardNumberEntryView.getText())
                .setExpiryDate(expiryDateEntryView.getText())
                .setSecurityCode(securityCodeEntryView.getText());

        if (judo.isAvsEnabled()) {
            cardBuilder.setAddress(new Address.Builder()
                    .setPostCode(postcodeEntryView.getText())
                    .setCountryCode(Country.codeFromCountry((String) countrySpinner.getSelectedItem()))
                    .build());
        }

        if (cardNumberEntryView.getCardType() == CardNetwork.MAESTRO) {
            cardBuilder.setIssueNumber(issueNumberEntryView.getText())
                    .setStartDate(startDateEntryView.getText());
        }

        if (cardEntryListener != null) {
            UserAnalytics identifiers = new UserAnalytics.Builder()
                    .setAppResumed(appResumeDetector.getResumedTimings())
                    .setCompletedFields(completedFieldsDetector.getFieldsOrderedByCompletion())
                    .setPastedFields(pastedFieldsDetector.getPasteTimings())
                    .setTotalKeystrokes(keystrokesDetector.getTotalKeystrokes())
                    .build();

            cardEntryListener.onSubmit(cardBuilder.build(), identifiers.toMap());
        }
    }

    public static CardEntryFragment newInstance(Judo judo, CardEntryListener listener) {
        CardEntryFragment cardEntryFragment = new CardEntryFragment();
        cardEntryFragment.setCardEntryListener(listener);

        Bundle arguments = new Bundle();
        arguments.putParcelable(Judo.JUDO_OPTIONS, judo);
        cardEntryFragment.setArguments(arguments);

        return cardEntryFragment;
    }

    @Override
    public void onValidate(boolean valid) {
        submitButton.setVisibility(valid ? View.VISIBLE : View.GONE);
    }
}