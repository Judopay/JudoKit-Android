package com.judopay.card;

import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.R;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Country;
import com.judopay.validation.CountryAndPostcodeValidator;
import com.judopay.validation.SecurityCodeValidator;
import com.judopay.validation.Validation;
import com.judopay.validation.ValidationAutoAdvanceManager;
import com.judopay.validation.ValidationManager;
import com.judopay.validation.Validator;
import com.judopay.view.CardNumberEntryView;
import com.judopay.view.CountrySpinnerAdapter;
import com.judopay.view.ExpiryDateEntryView;
import com.judopay.view.PostcodeEntryView;
import com.judopay.view.SecurityCodeEntryView;
import com.judopay.view.SingleClickOnClickListener;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.observables.ConnectableObservable;

import static com.judopay.Judo.isAvsEnabled;

public class TokenCardEntryFragment extends AbstractCardEntryFragment {

    private CardNumberEntryView cardNumberEntryView;
    private ExpiryDateEntryView expiryDateEntryView;
    private SecurityCodeEntryView securityCodeEntryView;
    private PostcodeEntryView postcodeEntryView;
    private Spinner countrySpinner;
    private View secureServerText;
    private Button paymentButton;
    private View countryAndPostcodeContainer;

    private ValidationManager validationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_token_card_entry, container, false);

        this.cardNumberEntryView = (CardNumberEntryView) view.findViewById(R.id.card_number_entry_view);
        this.expiryDateEntryView = (ExpiryDateEntryView) view.findViewById(R.id.expiry_date_entry_view);
        this.securityCodeEntryView = (SecurityCodeEntryView) view.findViewById(R.id.security_code_entry_view);
        this.postcodeEntryView = (PostcodeEntryView) view.findViewById(R.id.postcode_entry_view);
        this.countryAndPostcodeContainer = view.findViewById(R.id.country_postcode_container);

        this.countrySpinner = (Spinner) view.findViewById(R.id.country_spinner);
        this.secureServerText = view.findViewById(R.id.secure_server_text);
        this.paymentButton = (Button) view.findViewById(R.id.payment_button);

        return view;
    }

    @Override
    protected void onInitialize(JudoOptions options) {
        CardToken cardToken = options.getCardToken();

        if (cardToken == null) {
            throw new IllegalArgumentException("CardToken is required in JudoOptions for TokenCardEntryFragment");
        }

        if (options.isSecureServerMessageShown()) {
            secureServerText.setVisibility(View.VISIBLE);
        } else {
            secureServerText.setVisibility(View.GONE);
        }

        initializeInputs(cardToken, options);
        initializePayButton();
        initializeCountry();

        initializeValidators(cardToken);
    }

    @Override
    public void setCard(Card card) {
        // todo - throw error that this is not supported
    }

    private void initializeCountry() {
        countrySpinner.setAdapter(new CountrySpinnerAdapter(getActivity(), Country.avsCountries()));
    }

    private void initializeInputs(CardToken cardToken, JudoOptions options) {
        cardNumberEntryView.setCardType(cardToken.getType(), false);
        securityCodeEntryView.setCardType(cardToken.getType(), false);
        securityCodeEntryView.requestFocus();

        expiryDateEntryView.setExpiryDate(options.getCardToken().getFormattedEndDate());
        expiryDateEntryView.setEnabled(false);
        cardNumberEntryView.setTokenCard(options.getCardToken());
    }

    private void initializeValidators(CardToken cardToken) {
        List<Validator> validators = new ArrayList<>();
        List<Pair<Validator, View>> validatorViews = new ArrayList<>();

        SecurityCodeValidator securityCodeValidator = new SecurityCodeValidator(securityCodeEntryView.getEditText());
        securityCodeValidator.setCardType(cardToken.getType());
        securityCodeValidator.onValidate()
                .subscribe(new Action1<Validation>() {
                    @Override
                    public void call(Validation validation) {
                        if (isAvsEnabled()) {
                            countryAndPostcodeContainer.setVisibility(validation.isValid() ? View.VISIBLE : View.GONE);
                        }
                    }
                });

        validators.add(securityCodeValidator);
        validatorViews.add(new Pair<Validator, View>(securityCodeValidator, securityCodeEntryView.getEditText()));

        validationManager = new ValidationManager(validators, this);

        if (isAvsEnabled()) {
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

    @Override
    public void onValidate(boolean valid) {
        paymentButton.setVisibility(valid ? View.VISIBLE : View.GONE);
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
                .setCardNumber(cardNumberEntryView.getText())
                .setExpiryDate(expiryDateEntryView.getText())
                .setSecurityCode(securityCodeEntryView.getText());

        Address.Builder addressBuilder = new Address.Builder()
                .setPostCode(postcodeEntryView.getText());

        if (isAvsEnabled()) {
            addressBuilder.setCountryCode(Country.codeFromCountry((String) countrySpinner.getSelectedItem()));
        }

        cardBuilder.setCardAddress(addressBuilder.build());

        if (cardEntryListener != null) {
            cardEntryListener.onSubmit(cardBuilder.build());
        }
    }

    public static TokenCardEntryFragment newInstance(JudoOptions judoOptions, CardEntryListener listener) {
        TokenCardEntryFragment cardEntryFragment = new TokenCardEntryFragment();
        cardEntryFragment.setCardEntryListener(listener);

        Bundle arguments = new Bundle();
        arguments.putParcelable(Judo.JUDO_OPTIONS, judoOptions);
        cardEntryFragment.setArguments(arguments);

        return cardEntryFragment;
    }

}
