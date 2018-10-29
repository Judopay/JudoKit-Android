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
import android.widget.Spinner;

import com.judopay.Judo;
import com.judopay.R;
import com.judopay.arch.ThemeUtil;
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

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;

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

    private CompositeDisposable disposables = new CompositeDisposable();

    public static CardEntryFragment newInstance(Judo judo, CardEntryListener listener) {
        CardEntryFragment cardEntryFragment = new CardEntryFragment();
        cardEntryFragment.setCardEntryListener(listener);

        Bundle arguments = new Bundle();
        arguments.putParcelable(Judo.JUDO_OPTIONS, judo);
        cardEntryFragment.setArguments(arguments);

        return cardEntryFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_entry, container, false);

        submitButton = view.findViewById(R.id.button);

        securityCodeEntryView = view.findViewById(R.id.security_code_entry_view);
        cardNumberEntryView = view.findViewById(R.id.card_number_entry_view);
        expiryDateEntryView = view.findViewById(R.id.expiry_date_entry_view);

        postcodeEntryView = view.findViewById(R.id.postcode_entry_view);

        countrySpinner = view.findViewById(R.id.country_spinner);
        startDateEntryView = view.findViewById(R.id.start_date_entry_view);

        issueNumberEntryView = view.findViewById(R.id.issue_number_entry_view);

        startDateAndIssueNumberContainer = view.findViewById(R.id.start_date_issue_number_container);
        countryAndPostcodeContainer = view.findViewById(R.id.country_postcode_container);
        secureServerText = view.findViewById(R.id.secure_server_text);

        return view;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState, final Judo judo) {
        if (judo.getCardScanningIntent() != null) {
            cardNumberEntryView.setScanCardListener(() -> {
                PendingIntent cardScanningIntent = judo.getCardScanningIntent();
                if (cardScanningIntent != null && getActivity() != null) {
                    IntentSender intentSender = cardScanningIntent.getIntentSender();
                    try {
                        getActivity().startIntentSenderForResult(intentSender, Judo.CARD_SCANNING_REQUEST, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException ignore) {
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

        if (getActivity() != null) {
            boolean secureServerMessageShown = ThemeUtil.getBooleanAttr(getActivity(), R.attr.secureServerMessageShown);
            if (secureServerMessageShown) {
                secureServerText.setVisibility(View.VISIBLE);
            } else {
                secureServerText.setVisibility(View.GONE);
            }
        }

        initializeCountry();
        initializeValidators(judo);
        initializeButton(judo);
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
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
            if (getActivity() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        }
    }

    private void initializeValidators(final Judo judo) {
        List<Validator> validators = new ArrayList<>();
        List<Pair<Validator, View>> validatorViews = new ArrayList<>();

        cardNumberEntryView.addTextChangedListener(new SimpleTextWatcher() {
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
        disposables.add(cardNumberValidator.onValidate().subscribe(validation -> cardNumberEntryView.setValidation(validation)));

        validators.add(cardNumberValidator);
        validatorViews.add(new Pair<>(cardNumberValidator, cardNumberEntryView.getEditText()));

        startDateValidator = getStartDateValidator();
        validatorViews.add(new Pair<>(startDateValidator, startDateEntryView.getEditText()));

        issueNumberValidator = getIssueNumberValidator();
        validatorViews.add(new Pair<>(issueNumberValidator, issueNumberEntryView.getEditText()));

        ExpiryDateValidator expiryDateValidator = getExpiryDateValidator();
        validators.add(expiryDateValidator);
        validatorViews.add(new Pair<>(expiryDateValidator, expiryDateEntryView.getEditText()));

        securityCodeValidator = new SecurityCodeValidator(securityCodeEntryView.getEditText());
        validators.add(securityCodeValidator);
        validatorViews.add(new Pair<>(securityCodeValidator, securityCodeEntryView.getEditText()));

        validationManager = new ValidationManager(validators, this);

        if (judo.isAvsEnabled()) {
            ArrayList<Validator> avsValidators = new ArrayList<>();
            avsValidators.add(cardNumberValidator);
            avsValidators.add(expiryDateValidator);
            avsValidators.add(securityCodeValidator);

            avsValidationManager = new ValidationManager(avsValidators, valid -> countryAndPostcodeContainer.setVisibility(valid ? View.VISIBLE : View.GONE));
            initializeAvsValidators(validatorViews);
        }

        ValidationAutoAdvanceManager.bind(validationManager, validatorViews);
    }

    private void initializeAvsValidators(List<Pair<Validator, View>> validatorViews) {
        CountryAndPostcodeValidator countryAndPostcodeValidator = new CountryAndPostcodeValidator(countrySpinner, postcodeEntryView.getEditText());
        ConnectableObservable<Validation> observable = countryAndPostcodeValidator.onValidate();

        disposables.add(observable.subscribe(validation -> postcodeEntryView.setError(validation.getError(), validation.isShowError())));

        disposables.add(observable.subscribe(validation -> {
            String country = (String) countrySpinner.getSelectedItem();
            postcodeEntryView.setCountry(country);
        }));

        validationManager.addValidator(countryAndPostcodeValidator, observable);
        validatorViews.add(new Pair<>(countryAndPostcodeValidator, postcodeEntryView.getEditText()));

        observable.connect();
    }

    private ExpiryDateValidator getExpiryDateValidator() {
        ExpiryDateValidator expiryDateValidator = new ExpiryDateValidator(expiryDateEntryView.getEditText());
        disposables.add(expiryDateValidator.onValidate().subscribe(validation -> expiryDateEntryView.setValidation(validation)));
        return expiryDateValidator;
    }

    private IssueNumberValidator getIssueNumberValidator() {
        return new IssueNumberValidator(issueNumberEntryView.getEditText());
    }

    private StartDateValidator getStartDateValidator() {
        StartDateValidator startDateValidator = new StartDateValidator(startDateEntryView.getEditText());
        disposables.add(startDateValidator.onValidate().subscribe(validation -> startDateEntryView.setValidation(validation)));
        return startDateValidator;
    }

    private void initializeButton(final Judo judo) {
        submitButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                if (getActivity() != null) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        view.clearFocus();
                    }

                    hideKeyboard();
                    submitForm(judo);
                }
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
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
            cardEntryListener.onSubmit(cardBuilder.build());
        }
    }

    @Override
    public void onValidate(boolean valid) {
        submitButton.setVisibility(valid ? View.VISIBLE : View.GONE);
    }
}
