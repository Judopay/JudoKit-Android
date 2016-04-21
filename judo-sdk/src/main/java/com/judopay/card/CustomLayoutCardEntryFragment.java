package com.judopay.card;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.judopay.CustomLayout;
import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.R;
import com.judopay.model.Card;
import com.judopay.model.CardType;
import com.judopay.validation.CardNumberValidator;
import com.judopay.validation.CountryAndPostcodeValidator;
import com.judopay.validation.ExpiryDateValidator;
import com.judopay.validation.IssueNumberValidator;
import com.judopay.validation.SecurityCodeValidator;
import com.judopay.validation.StartDateValidator;
import com.judopay.validation.Validation;
import com.judopay.validation.ValidationManager;
import com.judopay.validation.Validator;
import com.judopay.view.HintFocusListener;
import com.judopay.view.NumberFormatTextWatcher;
import com.judopay.view.SimpleTextWatcher;
import com.judopay.view.SingleClickOnClickListener;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static com.judopay.Judo.JUDO_OPTIONS;

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
        initializeValidators();
        initializePayButton();
    }

    private void initializeInputTexts() {
        EditText cardNumberTextInputEditText = cardNumberTextInput.getEditText();

        if (cardNumberTextInputEditText != null) {
            cardNumberTextInputEditText.setOnFocusChangeListener(new HintFocusListener(cardNumberTextInputEditText, R.string.card_number_format));
            NumberFormatTextWatcher numberFormatTextWatcher = new NumberFormatTextWatcher(cardNumberTextInputEditText, getResources().getString(R.string.card_number_format));
            cardNumberTextInputEditText.addTextChangedListener(numberFormatTextWatcher);
        }

        EditText expiryDateEditText = expiryDateTextInput.getEditText();
        if (expiryDateEditText != null) {
            initializeDateEditText(expiryDateEditText);
        }

        EditText startDateEditText = startDateTextInput.getEditText();
        if (startDateEditText != null) {
            initializeDateEditText(startDateEditText);
        }
    }

    private void initializeDateEditText(EditText editText) {
        HintFocusListener hintFocusListener = new HintFocusListener(editText, R.string.date_hint);
        editText.setOnFocusChangeListener(hintFocusListener);

        String dateFormat = getResources().getString(R.string.date_format);
        NumberFormatTextWatcher numberFormatTextWatcher = new NumberFormatTextWatcher(editText, dateFormat, true);
        editText.addTextChangedListener(numberFormatTextWatcher);
    }

    private void initializeValidators() {
        List<Validator> validators = new ArrayList<>();

        EditText editText = cardNumberTextInput.getEditText();
        if (editText != null) {
            editText.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                protected void onTextChanged(CharSequence text) {
                    int cardType = CardType.fromCardNumber(text.toString());
                    if (Judo.isMaestroEnabled() && cardType == CardType.MAESTRO) {
                        validationManager.addValidator(issueNumberValidator);
                        validationManager.addValidator(startDateValidator);
                    } else {
                        validationManager.removeValidator(startDateValidator);
                        validationManager.removeValidator(issueNumberValidator);
                    }
                }
            });
        }

        CardNumberValidator cardNumberValidator = getCardNumberValidator();
        validators.add(cardNumberValidator);

        validators.add(new SecurityCodeValidator(securityCodeTextInput.getEditText()));

        ExpiryDateValidator expiryDateValidator = getExpiryDateValidator();
        validators.add(expiryDateValidator);

        issueNumberValidator = getIssueNumberValidator();
        startDateValidator = getStartDateValidator();

        if (Judo.isAvsEnabled()) {
            CountryAndPostcodeValidator countryAndPostcodeValidator = getCountryAndPostcodeValidator();
            validators.add(countryAndPostcodeValidator);
        }

        validationManager = new ValidationManager(validators, this);
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

    private CountryAndPostcodeValidator getCountryAndPostcodeValidator() {
        CountryAndPostcodeValidator countryAndPostcodeValidator = new CountryAndPostcodeValidator(countrySpinner, postcodeTextInput.getEditText());
        countryAndPostcodeValidator.onValidate()
                .subscribe(new Action1<Validation>() {
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
        return countryAndPostcodeValidator;
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

//        Address.Builder addressBuilder = new Address.Builder()
//                .setPostCode(postcodeEntryView.getText());
//
//        if (isAvsEnabled()) {
//            addressBuilder.setCountryCode(countrySpinner.getSelectedCountry().getCode());
//        }

//        cardBuilder.setCardAddress(addressBuilder.build());

//        if (cardNumberEntryView.getCardType() == CardType.MAESTRO) {
//            cardBuilder.setIssueNumber(issueNumberEntryView.getText())
//                    .setStartDate(startDateEntryView.getText());
//        }

        if (cardEntryListener != null) {
            cardEntryListener.onSubmit(cardBuilder.build());
        }
    }

    private String getText(TextInputLayout inputLayout) {
        return inputLayout.getEditText().getText().toString();
    }

    public static CustomLayoutCardEntryFragment newInstance(JudoOptions judoOptions, CardEntryListener listener) {
        CustomLayoutCardEntryFragment cardEntryFragment = new CustomLayoutCardEntryFragment();
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