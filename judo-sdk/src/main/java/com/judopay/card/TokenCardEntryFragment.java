package com.judopay.card;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.judopay.Judo;
import com.judopay.R;
import com.judopay.arch.ThemeUtil;
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

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;

public class TokenCardEntryFragment extends AbstractCardEntryFragment {
    private CardNumberEntryView cardNumberEntryView;
    private ExpiryDateEntryView expiryDateEntryView;
    private SecurityCodeEntryView securityCodeEntryView;
    private PostcodeEntryView postcodeEntryView;
    private Spinner countrySpinner;
    private View secureServerText;
    private View countryAndPostcodeContainer;
    private ValidationManager validationManager;

    private CompositeDisposable disposables = new CompositeDisposable();

    public static TokenCardEntryFragment newInstance(Judo judo, CardEntryListener listener) {
        TokenCardEntryFragment cardEntryFragment = new TokenCardEntryFragment();
        cardEntryFragment.setCardEntryListener(listener);

        Bundle arguments = new Bundle();
        arguments.putParcelable(Judo.JUDO_OPTIONS, judo);
        cardEntryFragment.setArguments(arguments);

        return cardEntryFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_token_card_entry, container, false);

        this.submitButton = view.findViewById(R.id.button);
        this.cardNumberEntryView = view.findViewById(R.id.card_number_entry_view);
        this.expiryDateEntryView = view.findViewById(R.id.expiry_date_entry_view);
        this.securityCodeEntryView = view.findViewById(R.id.security_code_entry_view);
        this.postcodeEntryView = view.findViewById(R.id.postcode_entry_view);
        this.countryAndPostcodeContainer = view.findViewById(R.id.country_postcode_container);

        this.countrySpinner = view.findViewById(R.id.country_spinner);
        this.secureServerText = view.findViewById(R.id.secure_server_text);

        return view;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState, Judo judo) {
        CardToken cardToken = judo.getCardToken();

        if (cardToken == null) {
            throw new IllegalArgumentException("CardToken is required in Judo for TokenCardEntryFragment");
        }

        if (getActivity() != null) {
            boolean secureServerMessageShown = ThemeUtil.getBooleanAttr(getActivity(), R.attr.secureServerMessageShown);
            if (secureServerMessageShown) {
                secureServerText.setVisibility(View.VISIBLE);
            } else {
                secureServerText.setVisibility(View.GONE);
            }
        }

        initializeInputs(cardToken, judo);
        initializePayButton(judo);
        initializeCountry();

        initializeValidators(cardToken, judo);
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }

    private void initializeCountry() {
        countrySpinner.setAdapter(new CountrySpinnerAdapter(getActivity(), Country.avsCountries()));
    }

    private void initializeInputs(CardToken cardToken, Judo options) {
        cardNumberEntryView.setCardType(cardToken.getType(), false);
        securityCodeEntryView.setHelperText(R.string.please_reenter_the_card_security_code);
        securityCodeEntryView.setCardType(cardToken.getType(), false);
        securityCodeEntryView.requestFocus();

        expiryDateEntryView.setExpiryDate(options.getCardToken().getFormattedEndDate());
        expiryDateEntryView.setEnabled(false);
        cardNumberEntryView.setTokenCard(options.getCardToken());
    }

    private void initializeValidators(CardToken cardToken, final Judo judo) {
        List<Validator> validators = new ArrayList<>();
        List<Pair<Validator, View>> validatorViews = new ArrayList<>();

        SecurityCodeValidator securityCodeValidator = new SecurityCodeValidator(securityCodeEntryView.getEditText());
        securityCodeValidator.setCardType(cardToken.getType());
        disposables.add(securityCodeValidator.onValidate()
                .subscribe(validation -> {
                    if (judo.isAvsEnabled()) {
                        countryAndPostcodeContainer.setVisibility(validation.isValid() ? View.VISIBLE : View.GONE);
                    }
                }));

        validators.add(securityCodeValidator);
        validatorViews.add(new Pair<>(securityCodeValidator, securityCodeEntryView.getEditText()));

        validationManager = new ValidationManager(validators, this);

        if (judo.isAvsEnabled()) {
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

    @Override
    public void onValidate(boolean valid) {
        submitButton.setVisibility(valid ? View.VISIBLE : View.GONE);
    }

    private void initializePayButton(final Judo judo) {
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

        if (cardEntryListener != null) {
            cardEntryListener.onSubmit(cardBuilder.build());
        }
    }
}
