package com.judopay.card;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.judopay.model.CustomLayout;
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
import com.judopay.validation.ValidationManager;
import com.judopay.validation.Validator;
import com.judopay.view.CountrySpinnerAdapter;
import com.judopay.validation.ValidationAutoAdvanceManager;
import com.judopay.view.HintFocusListener;
import com.judopay.view.NumberFormatTextWatcher;
import com.judopay.view.SimpleTextWatcher;
import com.judopay.view.SingleClickOnClickListener;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.observables.ConnectableObservable;

import static com.judopay.Judo.JUDO_OPTIONS;
import static com.judopay.Judo.isAvsEnabled;

public final class CustomLayoutCardEntryFragment extends AbstractCardEntryFragment {

    private Button paymentButton;

    private TextInputLayout cardNumberTextInput;
    private TextInputLayout securityCodeTextInput;
    private TextInputLayout expiryDateTextInput;
    private TextInputLayout issueNumberTextInput;
    private TextInputLayout startDateTextInput;
    private TextInputLayout postcodeTextInput;

    private Spinner countrySpinner;

    private ValidationManager validationManager;
    private ValidationManager avsValidationManager;

    private StartDateValidator startDateValidator;
    private IssueNumberValidator issueNumberValidator;
    private HintFocusListener securityCodeHintFocusChangeListener;
    private SecurityCodeValidator securityCodeValidator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        JudoOptions options = getArguments().getParcelable(JUDO_OPTIONS);

        CustomLayout customLayout = options.getCustomLayout();
        View view = inflater.inflate(customLayout.getLayoutId(), container, false);

        paymentButton = (Button) view.findViewById(customLayout.getSubmitButton());
        cardNumberTextInput = (TextInputLayout) view.findViewById(customLayout.getCardNumberInput());
        securityCodeTextInput = (TextInputLayout) view.findViewById(customLayout.getSecurityCodeInput());
        expiryDateTextInput = (TextInputLayout) view.findViewById(customLayout.getExpiryDateInput());
        startDateTextInput = (TextInputLayout) view.findViewById(customLayout.getStartDateInput());
        issueNumberTextInput = (TextInputLayout) view.findViewById(customLayout.getIssueNumberInput());
        postcodeTextInput = (TextInputLayout) view.findViewById(customLayout.getPostcodeInput());
        countrySpinner = (Spinner) view.findViewById(customLayout.getCountrySpinner());

        return view;
    }

    @Override
    protected void onInitialize(JudoOptions options) {
        if (judoOptions.getButtonLabel() != null) {
            this.paymentButton.setText(judoOptions.getButtonLabel());
        }

        initializeInputTexts();
        initializeCountry();
        initializeValidators();
        initializePayButton();

        if (judoOptions.getCardNumber() != null) {
            EditText cardNumberEditText = cardNumberTextInput.getEditText();
            if (cardNumberEditText != null) {
                cardNumberEditText.setText(judoOptions.getCardNumber());
            }

            EditText expiryDateEditText = expiryDateTextInput.getEditText();
            if (expiryDateEditText != null) {
                expiryDateEditText.requestFocus();
            }
        }

        if (judoOptions.getExpiryYear() != null && judoOptions.getExpiryMonth() != null) {
            EditText expiryDateEditText = expiryDateTextInput.getEditText();
            if (expiryDateEditText != null) {
                expiryDateEditText.setText(getString(R.string.expiry_date_format, judoOptions.getExpiryMonth(), judoOptions.getExpiryYear()));
            }

            EditText securityCodeEditText = securityCodeTextInput.getEditText();
            if (securityCodeEditText != null) {
                securityCodeEditText.requestFocus();
            }
        }
    }

    @Override
    public void onValidate(boolean valid) {
        paymentButton.setVisibility(valid ? View.VISIBLE : View.GONE);
    }

    private void initializeInputTexts() {
        EditText cardNumberTextInputEditText = cardNumberTextInput.getEditText();

        if (cardNumberTextInputEditText != null) {
            cardNumberTextInputEditText.setOnFocusChangeListener(new HintFocusListener(cardNumberTextInputEditText, getResources().getString(R.string.card_number_format)));
            NumberFormatTextWatcher numberFormatTextWatcher = new NumberFormatTextWatcher(cardNumberTextInputEditText, getResources().getString(R.string.card_number_format));
            cardNumberTextInputEditText.addTextChangedListener(numberFormatTextWatcher);
        }

        EditText expiryDateEditText = expiryDateTextInput.getEditText();
        if (expiryDateEditText != null) {
            initializeDateEditText(expiryDateEditText);
        }

        startDateTextInput.setVisibility(View.GONE);
        EditText startDateEditText = startDateTextInput.getEditText();
        if (startDateEditText != null) {
            initializeDateEditText(startDateEditText);
        }

        issueNumberTextInput.setVisibility(View.GONE);

        EditText securityCodeEditText = securityCodeTextInput.getEditText();
        if (securityCodeEditText != null) {
            securityCodeHintFocusChangeListener = new HintFocusListener(securityCodeEditText, "000");
            securityCodeEditText.setOnFocusChangeListener(securityCodeHintFocusChangeListener);
        }

        countrySpinner.setVisibility(View.GONE);
        postcodeTextInput.setVisibility(View.GONE);
    }

    private void initializeDateEditText(EditText editText) {
        HintFocusListener hintFocusListener = new HintFocusListener(editText, getString(R.string.date_hint));
        editText.setOnFocusChangeListener(hintFocusListener);

        String dateFormat = getResources().getString(R.string.date_format);
        NumberFormatTextWatcher numberFormatTextWatcher = new NumberFormatTextWatcher(editText, dateFormat, true);
        editText.addTextChangedListener(numberFormatTextWatcher);
    }

    private void initializeValidators() {
        List<Validator> validators = new ArrayList<>();
        ArrayList<Pair<Validator, View>> validatorViews = new ArrayList<>();

        EditText editText = cardNumberTextInput.getEditText();
        if (editText != null) {
            editText.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                protected void onTextChanged(CharSequence text) {
                    int cardType = CardNetwork.fromCardNumber(text.toString());

                    String securityCode = CardNetwork.securityCode(cardType);
                    String securityCodeHint = CardNetwork.securityCodeHint(cardType);
                    securityCodeTextInput.setHint(securityCode);
                    securityCodeHintFocusChangeListener.setHint(securityCodeHint);
                    securityCodeValidator.setCardType(cardType);

                    EditText securityCodeTextInputEditText = securityCodeTextInput.getEditText();
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

                        startDateTextInput.setVisibility(View.VISIBLE);
                        issueNumberTextInput.setVisibility(View.VISIBLE);
                    } else {
                        validationManager.removeValidator(startDateValidator);
                        validationManager.removeValidator(issueNumberValidator);

                        if (isAvsEnabled()) {
                            avsValidationManager.removeValidator(startDateValidator);
                            avsValidationManager.removeValidator(issueNumberValidator);
                        }

                        startDateTextInput.setVisibility(View.GONE);
                        issueNumberTextInput.setVisibility(View.GONE);
                    }
                }
            });
        }

        CardNumberValidator cardNumberValidator = getCardNumberValidator();
        validators.add(cardNumberValidator);
        validatorViews.add(new Pair<Validator, View>(cardNumberValidator, cardNumberTextInput.getEditText()));

        startDateValidator = getStartDateValidator();
        validatorViews.add(new Pair<Validator, View>(startDateValidator, startDateTextInput.getEditText()));

        issueNumberValidator = getIssueNumberValidator();
        validatorViews.add(new Pair<Validator, View>(issueNumberValidator, issueNumberTextInput.getEditText()));

        ExpiryDateValidator expiryDateValidator = getExpiryDateValidator();
        validators.add(expiryDateValidator);
        validatorViews.add(new Pair<Validator, View>(expiryDateValidator, expiryDateTextInput.getEditText()));

        securityCodeValidator = new SecurityCodeValidator(securityCodeTextInput.getEditText());
        validators.add(securityCodeValidator);
        validatorViews.add(new Pair<Validator, View>(securityCodeValidator, securityCodeTextInput.getEditText()));

        validationManager = new ValidationManager(validators, this);

        if (isAvsEnabled()) {
            ArrayList<Validator> avsValidators = new ArrayList<>();
            avsValidators.add(cardNumberValidator);
            avsValidators.add(expiryDateValidator);
            avsValidators.add(securityCodeValidator);

            avsValidationManager = new ValidationManager(avsValidators, new ValidationManager.OnChangeListener() {
                @Override
                public void onValidate(boolean valid) {
                    countrySpinner.setVisibility(valid ? View.VISIBLE : View.GONE);
                    postcodeTextInput.setVisibility(valid ? View.VISIBLE : View.GONE);
                }
            });
            initializeAvsValidators(validatorViews);
        }

        new ValidationAutoAdvanceManager(validationManager, validatorViews);
    }

    private void initializeAvsValidators(List<Pair<Validator, View>> validatorViews) {
        CountryAndPostcodeValidator countryAndPostcodeValidator = new CountryAndPostcodeValidator(countrySpinner, postcodeTextInput.getEditText());
        ConnectableObservable<Validation> observable = countryAndPostcodeValidator.onValidate();

        observable.subscribe(new Action1<Validation>() {
            @Override
            public void call(Validation validation) {
                postcodeTextInput.setErrorEnabled(validation.isShowError());

                if (validation.isShowError()) {
                    postcodeTextInput.setError(getResources().getString(validation.getError()));
                } else {
                    postcodeTextInput.setError("");
                }
            }
        });

        observable.subscribe(new Action1<Validation>() {
            @Override
            public void call(Validation validation) {
                String country = (String) countrySpinner.getSelectedItem();
                postcodeTextInput.setHint(getResources().getString(Country.postcodeName(country)));
                boolean postcodeNumeric = Country.UNITED_STATES.equals(country);

                EditText editText = postcodeTextInput.getEditText();
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
        validatorViews.add(new Pair<Validator, View>(countryAndPostcodeValidator, postcodeTextInput.getEditText()));

        observable.connect();
    }

    private CardNumberValidator getCardNumberValidator() {
        CardNumberValidator cardNumberValidator = new CardNumberValidator(cardNumberTextInput.getEditText(), Judo.isMaestroEnabled(), Judo.isAmexEnabled());
        cardNumberValidator.onValidate()
                .subscribe(new Action1<Validation>() {
                    @Override
                    public void call(Validation validation) {
                        cardNumberTextInput.setErrorEnabled(validation.isShowError());

                        if (validation.isShowError()) {
                            cardNumberTextInput.setError(getString(validation.getError()));
                        } else {
                            cardNumberTextInput.setError("");
                        }
                    }
                });
        return cardNumberValidator;
    }

    private void initializeCountry() {
        countrySpinner.setAdapter(new CountrySpinnerAdapter(getActivity(), Country.avsCountries()));
    }

    private ExpiryDateValidator getExpiryDateValidator() {
        ExpiryDateValidator expiryDateValidator = new ExpiryDateValidator(expiryDateTextInput.getEditText());
        expiryDateValidator.onValidate()
                .subscribe(new Action1<Validation>() {
                    @Override
                    public void call(Validation validation) {
                        expiryDateTextInput.setErrorEnabled(validation.isShowError());

                        if (validation.isShowError()) {
                            expiryDateTextInput.setError(getResources().getString(validation.getError()));
                        } else {
                            expiryDateTextInput.setError("");
                        }
                    }
                });
        return expiryDateValidator;
    }

    private IssueNumberValidator getIssueNumberValidator() {
        return new IssueNumberValidator(issueNumberTextInput.getEditText());
    }

    private StartDateValidator getStartDateValidator() {
        StartDateValidator startDateValidator = new StartDateValidator(startDateTextInput.getEditText());
        startDateValidator.onValidate()
                .subscribe(new Action1<Validation>() {
                    @Override
                    public void call(Validation validation) {
                        startDateTextInput.setErrorEnabled(validation.isShowError());
                        if (validation.isShowError()) {
                            startDateTextInput.setError(getResources().getString(validation.getError()));
                        } else {
                            startDateTextInput.setError("");
                        }
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

    private void submitForm() {
        Card.Builder cardBuilder = new Card.Builder()
                .setCardNumber(getText(cardNumberTextInput))
                .setExpiryDate(getText(expiryDateTextInput))
                .setSecurityCode(getText(securityCodeTextInput));

        if (postcodeTextInput != null) {
            Address.Builder addressBuilder = new Address.Builder();

            if (isAvsEnabled()) {
                addressBuilder.setPostCode(getText(postcodeTextInput))
                        .setCountryCode(Country.codeFromCountry((String) countrySpinner.getSelectedItem()));
            }
            cardBuilder.setCardAddress(addressBuilder.build());
        }

        EditText cardNumberEditText = cardNumberTextInput.getEditText();
        if (cardNumberEditText != null && CardNetwork.fromCardNumber(getText(cardNumberTextInput)) == CardNetwork.MAESTRO) {
            cardBuilder.setIssueNumber(getText(issueNumberTextInput))
                    .setStartDate(getText(startDateTextInput));
        }

        if (cardEntryListener != null) {
            cardEntryListener.onSubmit(cardBuilder.build());
        }
    }

    private String getText(TextInputLayout inputLayout) {
        EditText editText = inputLayout.getEditText();
        return editText != null ? editText.getText().toString() : "";
    }

    public static CustomLayoutCardEntryFragment newInstance(JudoOptions judoOptions, CardEntryListener listener) {
        CustomLayoutCardEntryFragment cardEntryFragment = new CustomLayoutCardEntryFragment();
        cardEntryFragment.setCardEntryListener(listener);

        Bundle arguments = new Bundle();
        arguments.putParcelable(Judo.JUDO_OPTIONS, judoOptions);
        cardEntryFragment.setArguments(arguments);

        return cardEntryFragment;
    }

}