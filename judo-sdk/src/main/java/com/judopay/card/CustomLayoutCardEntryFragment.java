package com.judopay.card;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.judopay.Judo;
import com.judopay.R;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.CardNetwork;
import com.judopay.model.Country;
import com.judopay.model.CustomLayout;
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
import com.judopay.view.CountrySpinnerAdapter;
import com.judopay.view.HintFocusListener;
import com.judopay.view.JudoEditText;
import com.judopay.view.NumberFormatTextWatcher;
import com.judopay.view.SimpleTextWatcher;
import com.judopay.view.SingleClickOnClickListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;

import static com.judopay.Judo.JUDO_OPTIONS;
import static com.judopay.arch.TextUtil.isEmpty;

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
    private StartDateValidator startDateValidator;
    private IssueNumberValidator issueNumberValidator;
    private HintFocusListener securityCodeHintFocusChangeListener;
    private SecurityCodeValidator securityCodeValidator;

    private CompositeDisposable disposables = new CompositeDisposable();

    public static CustomLayoutCardEntryFragment newInstance(final Judo judo, final CardEntryListener listener) {
        CustomLayoutCardEntryFragment cardEntryFragment = new CustomLayoutCardEntryFragment();
        cardEntryFragment.setCardEntryListener(listener);

        Bundle arguments = new Bundle();
        arguments.putParcelable(JUDO_OPTIONS, judo);
        cardEntryFragment.setArguments(arguments);

        return cardEntryFragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new IllegalArgumentException("arguments missing");
        }

        Judo judo = getArguments().getParcelable(JUDO_OPTIONS);
        if (judo == null) {
            throw new IllegalArgumentException("Judo argument missing");
        }

        CustomLayout customLayout = judo.getCustomLayout();
        View view = inflater.inflate(customLayout.getLayoutId(), container, false);

        paymentButton = view.findViewById(customLayout.getSubmitButton());
        cardNumberTextInput = view.findViewById(customLayout.getCardNumberInput());
        securityCodeTextInput = view.findViewById(customLayout.getSecurityCodeInput());
        expiryDateTextInput = view.findViewById(customLayout.getExpiryDateInput());
        startDateTextInput = view.findViewById(customLayout.getStartDateInput());
        issueNumberTextInput = view.findViewById(customLayout.getIssueNumberInput());
        postcodeTextInput = view.findViewById(customLayout.getPostcodeInput());
        countrySpinner = view.findViewById(customLayout.getCountrySpinner());

        return view;
    }

    @Override
    protected void onInitialize(@Nullable final Bundle savedInstanceState, final Judo judo) {
        String buttonLabel = getButtonLabel();
        if (!isEmpty(buttonLabel)) {
            this.paymentButton.setText(buttonLabel);
        }

        initializeInputTexts();
        initializeCountry();
        initializeValidators(judo);
        initializePayButton(judo);

        if (judo.getCardNumber() != null) {
            EditText cardNumberEditText = cardNumberTextInput.getEditText();
            if (cardNumberEditText != null) {
                cardNumberEditText.setText(judo.getCardNumber());
            }

            EditText expiryDateEditText = expiryDateTextInput.getEditText();
            if (expiryDateEditText != null) {
                expiryDateEditText.requestFocus();
            }
        }

        if (judo.getExpiryYear() != null && judo.getExpiryMonth() != null) {
            EditText expiryDateEditText = expiryDateTextInput.getEditText();
            if (expiryDateEditText != null) {
                expiryDateEditText.setText(getString(R.string.expiry_date_format, judo.getExpiryMonth(), judo.getExpiryYear()));
            }

            EditText securityCodeEditText = securityCodeTextInput.getEditText();
            if (securityCodeEditText != null) {
                securityCodeEditText.requestFocus();
            }
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }

    @Override
    public void onValidate(final boolean valid) {
        paymentButton.setVisibility(valid ? View.VISIBLE : View.GONE);
    }

    private void initializeInputTexts() {
        JudoEditText cardNumberEditText = (JudoEditText) cardNumberTextInput.getEditText();

        if (cardNumberEditText != null) {
            initNumericEditText(cardNumberEditText, 19, "0123456789 ");

            cardNumberEditText.setOnFocusChangeListener(new HintFocusListener(cardNumberEditText, getResources().getString(R.string.card_number_format)));
            NumberFormatTextWatcher numberFormatTextWatcher = new NumberFormatTextWatcher(cardNumberEditText, getResources().getString(R.string.card_number_format));

            cardNumberEditText.addTextChangedListener(numberFormatTextWatcher);
        }

        JudoEditText expiryDateEditText = (JudoEditText) expiryDateTextInput.getEditText();

        if (expiryDateEditText != null) {
            initializeDateEditText(expiryDateEditText);
        }

        startDateTextInput.setVisibility(View.GONE);
        JudoEditText startDateEditText = (JudoEditText) startDateTextInput.getEditText();

        if (startDateEditText != null) {
            initializeDateEditText(startDateEditText);
        }

        issueNumberTextInput.setVisibility(View.GONE);
        EditText issueNumberEditText = issueNumberTextInput.getEditText();

        if (issueNumberEditText != null) {
            initNumericEditText(issueNumberEditText, 2, "0123456789");
        }

        EditText securityCodeEditText = securityCodeTextInput.getEditText();

        if (securityCodeEditText != null) {
            initNumericEditText(securityCodeEditText, 3, "0123456789");
            securityCodeHintFocusChangeListener = new HintFocusListener(securityCodeEditText, "000");
            securityCodeEditText.setOnFocusChangeListener(securityCodeHintFocusChangeListener);
        }

        countrySpinner.setVisibility(View.GONE);
        postcodeTextInput.setVisibility(View.GONE);

        EditText postcodeEditText = postcodeTextInput.getEditText();

        if (postcodeEditText != null) {
            postcodeEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        }
    }

    private void initializeDateEditText(final JudoEditText editText) {
        initNumericEditText(editText, 5, "0123456789/");

        HintFocusListener hintFocusListener = new HintFocusListener(editText, getString(R.string.date_hint));
        editText.setOnFocusChangeListener(hintFocusListener);

        String dateFormat = getResources().getString(R.string.date_format);
        NumberFormatTextWatcher numberFormatTextWatcher = new NumberFormatTextWatcher(editText, dateFormat);
        editText.addTextChangedListener(numberFormatTextWatcher);
    }

    private void initNumericEditText(final EditText editText, final int maxLength, final String acceptedChars) {
        editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setKeyListener(DigitsKeyListener.getInstance(acceptedChars));
    }

    private void initializeValidators(final Judo judo) {
        List<Validator> validators = new ArrayList<>();
        ArrayList<Pair<Validator, View>> validatorViews = new ArrayList<>();

        EditText editText = cardNumberTextInput.getEditText();
        if (editText != null) {
            editText.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                protected void onTextChanged(final CharSequence text) {
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

                    if (judo.isMaestroEnabled() && cardType == CardNetwork.MAESTRO) {
                        validationManager.addValidator(issueNumberValidator);
                        validationManager.addValidator(startDateValidator);

                        startDateTextInput.setVisibility(View.VISIBLE);
                        issueNumberTextInput.setVisibility(View.VISIBLE);
                    } else {
                        validationManager.removeValidator(startDateValidator);
                        validationManager.removeValidator(issueNumberValidator);

                        startDateTextInput.setVisibility(View.GONE);
                        issueNumberTextInput.setVisibility(View.GONE);
                    }
                }
            });
        }

        CardNumberValidator cardNumberValidator = getCardNumberValidator(judo);
        validators.add(cardNumberValidator);
        validatorViews.add(new Pair<>(cardNumberValidator, cardNumberTextInput.getEditText()));

        startDateValidator = getStartDateValidator();
        validatorViews.add(new Pair<>(startDateValidator, startDateTextInput.getEditText()));

        issueNumberValidator = getIssueNumberValidator();
        validatorViews.add(new Pair<>(issueNumberValidator, issueNumberTextInput.getEditText()));

        ExpiryDateValidator expiryDateValidator = getExpiryDateValidator();
        validators.add(expiryDateValidator);
        validatorViews.add(new Pair<>(expiryDateValidator, expiryDateTextInput.getEditText()));

        securityCodeValidator = new SecurityCodeValidator(securityCodeTextInput.getEditText());
        validators.add(securityCodeValidator);
        validatorViews.add(new Pair<>(securityCodeValidator, securityCodeTextInput.getEditText()));

        validationManager = new ValidationManager(validators, this);

        if (judo.isAvsEnabled()) {
            initializeAvsValidators(validatorViews, cardNumberValidator, expiryDateValidator);
        }

        ValidationAutoAdvanceManager.bind(validationManager, validatorViews);
    }

    private void initializeAvsValidators(final List<Pair<Validator, View>> validatorViews,
                                         final CardNumberValidator cardNumberValidator,
                                         final ExpiryDateValidator expiryDateValidator) {
        ArrayList<Validator> avsValidators = new ArrayList<>();
        avsValidators.add(cardNumberValidator);
        avsValidators.add(expiryDateValidator);
        avsValidators.add(securityCodeValidator);

        new ValidationManager(avsValidators, valid -> {
            countrySpinner.setVisibility(valid ? View.VISIBLE : View.GONE);
            postcodeTextInput.setVisibility(valid ? View.VISIBLE : View.GONE);
        });

        CountryAndPostcodeValidator countryAndPostcodeValidator = new CountryAndPostcodeValidator(countrySpinner, postcodeTextInput.getEditText());
        ConnectableObservable<Validation> observable = countryAndPostcodeValidator.onValidate();

        disposables.add(observable.subscribe(validation -> {
            postcodeTextInput.setErrorEnabled(validation.isShowError());

            if (validation.isShowError()) {
                postcodeTextInput.setError(getResources().getString(validation.getError()));
            } else {
                postcodeTextInput.setError("");
            }

            Country country = (Country) countrySpinner.getSelectedItem();
            postcodeTextInput.setHint(getResources().getString(country.getPostcodeNameResourceId()));
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
        }));

        validationManager.addValidator(countryAndPostcodeValidator, observable);
        validatorViews.add(new Pair<>(countryAndPostcodeValidator, postcodeTextInput.getEditText()));

        observable.connect();
    }

    private CardNumberValidator getCardNumberValidator(final Judo judo) {
        CardNumberValidator cardNumberValidator = new CardNumberValidator(cardNumberTextInput.getEditText(), judo.isMaestroEnabled(), judo.isAmexEnabled());
        disposables.add(cardNumberValidator.onValidate()
                .subscribe(validation -> {
                    cardNumberTextInput.setErrorEnabled(validation.isShowError());

                    if (validation.isShowError()) {
                        cardNumberTextInput.setError(getString(validation.getError()));
                    } else {
                        cardNumberTextInput.setError("");
                    }
                }));
        return cardNumberValidator;
    }

    private void initializeCountry() {
        countrySpinner.setAdapter(new CountrySpinnerAdapter(getActivity(), Country.values()));
    }

    private ExpiryDateValidator getExpiryDateValidator() {
        ExpiryDateValidator expiryDateValidator = new ExpiryDateValidator(expiryDateTextInput.getEditText());
        disposables.add(expiryDateValidator.onValidate()
                .subscribe(validation -> {
                    expiryDateTextInput.setErrorEnabled(validation.isShowError());

                    if (validation.isShowError()) {
                        expiryDateTextInput.setError(getResources().getString(validation.getError()));
                    } else {
                        expiryDateTextInput.setError("");
                    }
                }));
        return expiryDateValidator;
    }

    private IssueNumberValidator getIssueNumberValidator() {
        return new IssueNumberValidator(issueNumberTextInput.getEditText());
    }

    private StartDateValidator getStartDateValidator() {
        StartDateValidator startDateValidator = new StartDateValidator(startDateTextInput.getEditText());
        disposables.add(startDateValidator.onValidate()
                .subscribe(validation -> {
                    startDateTextInput.setErrorEnabled(validation.isShowError());
                    if (validation.isShowError()) {
                        startDateTextInput.setError(getResources().getString(validation.getError()));
                    } else {
                        startDateTextInput.setError("");
                    }
                }));
        return startDateValidator;
    }

    private void initializePayButton(final Judo judo) {
        paymentButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                hideKeyboard();
                submitForm(judo);
            }
        });
    }

    private void submitForm(final Judo judo) {
        Card.Builder cardBuilder = new Card.Builder()
                .setCardNumber(getText(cardNumberTextInput))
                .setExpiryDate(getText(expiryDateTextInput))
                .setSecurityCode(getText(securityCodeTextInput));

        if (judo.isAvsEnabled() && postcodeTextInput != null) {
            cardBuilder.setAddress(new Address.Builder()
                    .setPostCode(getText(postcodeTextInput))
                    .setCountryCode(((Country) countrySpinner.getSelectedItem()).getCountryCode())
                    .build());
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

    private String getText(final TextInputLayout inputLayout) {
        EditText editText = inputLayout.getEditText();
        return editText != null ? editText.getText().toString() : "";
    }
}
