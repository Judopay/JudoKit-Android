package com.judopay.card;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.judopay.CardNumberValidation;
import com.judopay.CountryAndPostcodeValidation;
import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.PaymentForm;
import com.judopay.PaymentFormValidation;
import com.judopay.R;
import com.judopay.StartDateAndIssueNumberValidation;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.CardType;
import com.judopay.model.Country;
import com.judopay.view.CardNumberEntryView;
import com.judopay.view.CompositeOnFocusChangeListener;
import com.judopay.view.CountrySpinner;
import com.judopay.view.CvvImageView;
import com.judopay.view.DateSeparatorTextWatcher;
import com.judopay.view.EmptyTextHintOnFocusChangeListener;
import com.judopay.view.ExpiryDateEntryView;
import com.judopay.view.HidingViewTextWatcher;
import com.judopay.view.HintFocusListener;
import com.judopay.view.ScrollOnFocusChangeListener;
import com.judopay.view.SimpleTextWatcher;
import com.judopay.view.SingleClickOnClickListener;

import static com.judopay.Judo.isAvsEnabled;

public final class CardEntryFragment extends Fragment {

    private EditText cvvEditText;
    private View cvvHelperText;
    private Button paymentButton;
    private CvvImageView cvvImageView;
    private CountrySpinner countrySpinner;
    private EditText postcodeEditText;
    private EditText startDateEditText;
    private EditText issueNumberEditText;
    private TextInputLayout cvvInputLayout;
    private CardNumberEntryView cardNumberEntryView;
    private TextInputLayout startDateInputLayout;
    private TextInputLayout postcodeInputLayout;
    private View startDateAndIssueNumberContainer;
    private View countryAndPostcodeContainer;
    private HintFocusListener cvvHintChangeListener;
    private ScrollView scrollView;
    private View cardsAcceptedErrorText;
    private View issueNumberHelperText;

    private JudoOptions judoOptions;
    private CardEntryListener cardEntryListener;
    private ExpiryDateEntryView expiryDateEntryView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_entry, container, false);

        paymentButton = (Button) view.findViewById(R.id.payment_button);

        cvvEditText = (EditText) view.findViewById(R.id.cvv_edit_text);
        cvvInputLayout = (TextInputLayout) view.findViewById(R.id.cvv_input_layout);
        cvvHelperText = view.findViewById(R.id.cvv_helper_text);

        postcodeEditText = (EditText) view.findViewById(R.id.post_code_edit_text);
        postcodeInputLayout = (TextInputLayout) view.findViewById(R.id.post_code_input_layout);

        cardNumberEntryView = (CardNumberEntryView) view.findViewById(R.id.card_number_entry_view);
        expiryDateEntryView = (ExpiryDateEntryView) view.findViewById(R.id.expiry_date_entry_view);

        startDateEditText = (EditText) view.findViewById(R.id.start_date_edit_text);
        startDateInputLayout = (TextInputLayout) view.findViewById(R.id.start_date_input_layout);

        countrySpinner = (CountrySpinner) view.findViewById(R.id.country_spinner);

        cvvImageView = (CvvImageView) view.findViewById(R.id.cvv_image_view);

        issueNumberEditText = (EditText) view.findViewById(R.id.issue_number_edit_text);
        issueNumberHelperText = view.findViewById(R.id.issue_number_helper_text);

        startDateAndIssueNumberContainer = view.findViewById(R.id.start_date_issue_number_container);
        countryAndPostcodeContainer = view.findViewById(R.id.country_postcode_container);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);

        cardsAcceptedErrorText = view.findViewById(R.id.cards_accepted_error_text);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleTextWatcher validationWatcher = new SimpleTextWatcher() {
            @Override
            protected void onTextChanged() {
                updateFormView();
            }
        };

        if (getArguments() != null && getArguments().containsKey(Judo.JUDO_OPTIONS)) {
            this.judoOptions = getArguments().getParcelable(Judo.JUDO_OPTIONS);

            if (judoOptions != null) {
                if (judoOptions.getButtonLabel() != null) {
                    this.paymentButton.setText(judoOptions.getButtonLabel());
                }

                if (judoOptions.getCardToken() != null) {
                    cardNumberEntryView.setCardType(judoOptions.getCardToken().getType());
                    cvvEditText.requestFocus();
                }

                if (judoOptions.getCardNumber() != null) {
                    cardNumberEntryView.setText(judoOptions.getCardNumber());

                    int cardType = CardType.fromCardNumber(judoOptions.getCardNumber());
                    cardNumberEntryView.setCardType(cardType);
                }

                if (judoOptions.getExpiryYear() != null && judoOptions.getExpiryMonth() != null) {
                    expiryDateEntryView.setText(getString(R.string.expiry_date_format, judoOptions.getExpiryMonth(), judoOptions.getExpiryYear()));
                }

                if (judoOptions.getCvv() != null) {
                    cvvEditText.setText(judoOptions.getCvv());
                }
            }
        }

        initialiseCardNumber(validationWatcher);
        initialiseExpiryDate(validationWatcher);
        initialiseCvv(validationWatcher);

        initialiseStartDate(validationWatcher);
        initialiseIssueNumber(validationWatcher);

        initialisePostcode(validationWatcher);
        initialiseCountry();

        initialisePayButton();
    }

    private void initialisePayButton() {
        paymentButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                hideKeyboard();
                submitForm();
            }
        });
    }

    private void initialiseCountry() {
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFormView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initialisePostcode(SimpleTextWatcher formValidator) {
        postcodeEditText.setOnFocusChangeListener(new CompositeOnFocusChangeListener(
                new HintFocusListener(postcodeEditText, R.string.empty),
                new ScrollOnFocusChangeListener(scrollView))
        );

        postcodeEditText.addTextChangedListener(formValidator);
    }

    private void initialiseIssueNumber(SimpleTextWatcher formValidator) {
        issueNumberEditText.setOnFocusChangeListener(new CompositeOnFocusChangeListener(
                new EmptyTextHintOnFocusChangeListener(issueNumberHelperText),
                new HintFocusListener(issueNumberEditText, R.string.issue_number_hint)
        ));

        issueNumberEditText.addTextChangedListener(formValidator);
        issueNumberEditText.addTextChangedListener(new HidingViewTextWatcher(issueNumberHelperText));
    }

    private void initialiseCvv(SimpleTextWatcher formValidator) {
        cvvHintChangeListener = new HintFocusListener(cvvEditText, R.string.cvv_hint);
        cvvEditText.setOnFocusChangeListener(new CompositeOnFocusChangeListener(
                new EmptyTextHintOnFocusChangeListener(cvvHelperText),
                cvvHintChangeListener
        ));

        cvvEditText.addTextChangedListener(formValidator);
        cvvEditText.addTextChangedListener(new HidingViewTextWatcher(cvvHelperText));
    }

    private void initialiseStartDate(SimpleTextWatcher formValidator) {
        startDateEditText.setOnFocusChangeListener(new HintFocusListener(startDateEditText, R.string.date_hint));
        startDateEditText.addTextChangedListener(formValidator);
        startDateEditText.addTextChangedListener(new DateSeparatorTextWatcher(startDateEditText));
    }

    private void initialiseExpiryDate(SimpleTextWatcher formValidator) {
        if (judoOptions.getCardToken() == null) {
            expiryDateEntryView.addTextChangedListener(formValidator);
        } else {
            expiryDateEntryView.setTokenized(true);
        }
    }

    private void initialiseCardNumber(SimpleTextWatcher formValidator) {
        if (judoOptions.getCardToken() == null) {
            cardNumberEntryView.addTextChangedListener(formValidator);
        } else {
            cardNumberEntryView.setTokenizedNumber(judoOptions.getCardToken().getLastFour());
        }
    }

    private void hideKeyboard() {
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void updateFormView() {
        PaymentForm.Builder builder = new PaymentForm.Builder()
                .setCardNumber(cardNumberEntryView.getText())
                .setCvv(getCvv())
                .setCountry(getCountry())
                .setPostcode(getPostcode())
                .setIssueNumber(getIssueNumber())
                .setExpiryDate(expiryDateEntryView.getText())
                .setStartDate(trim(startDateEditText))
                .setAddressRequired(Judo.isAvsEnabled())
                .setAmexSupported(Judo.isAmexEnabled())
                .setMaestroSupported(Judo.isMaestroEnabled());

        CardToken cardToken = judoOptions.getCardToken();

        if (cardToken != null) {
            builder.setTokenCard(true)
                    .setCardType(cardToken.getType());
        }

        PaymentFormValidation formView = new PaymentFormValidation.Builder()
                .build(builder.build());

        if (cardToken == null) {
            cardNumberEntryView.setCardType(formView.getCardType());
        }

        updateFormErrors(formView);
        moveFieldFocus(formView);
    }

    private void updateFormErrors(PaymentFormValidation formView) {
        showCardNumberErrors(formView.getCardNumberValidation());

        showExpiryDateErrors(formView);

        showStartDateAndIssueNumberErrors(formView.getStartDateAndIssueNumberState());

        updateCvvErrors(formView);

        updateCountryAndPostcode(formView.getCountryAndPostcodeValidation());

        paymentButton.setVisibility(formView.isPaymentButtonEnabled() ? View.VISIBLE : View.GONE);
    }

    private void updateCvvErrors(PaymentFormValidation formView) {
        cvvInputLayout.setHint(getString(formView.getCvvLabel()));
        cvvHintChangeListener.setHintResourceId(formView.getCvvHint());
        cvvEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(formView.getCvvLength())});
        cvvImageView.setCardType(formView.getCardType());
    }

    private void showStartDateAndIssueNumberErrors(StartDateAndIssueNumberValidation startDateAndIssueNumberValidation) {
        startDateInputLayout.setErrorEnabled(startDateAndIssueNumberValidation.isShowStartDateError());

        if (startDateAndIssueNumberValidation.isShowStartDateError()) {
            startDateInputLayout.setError(getString(startDateAndIssueNumberValidation.getStartDateError()));
        } else {
            startDateInputLayout.setError("");
        }

        startDateAndIssueNumberContainer.setVisibility(startDateAndIssueNumberValidation.isShowIssueNumberAndStartDate()
                ? View.VISIBLE : View.GONE);
    }

    private void updateCountryAndPostcode(CountryAndPostcodeValidation validation) {
        countryAndPostcodeContainer.setVisibility(validation.isShowCountryAndPostcode() ? View.VISIBLE : View.GONE);

        postcodeInputLayout.setErrorEnabled(validation.isShowPostcodeError());
        postcodeInputLayout.setHint(getString(validation.getPostcodeLabel()));

        if (validation.isShowPostcodeError()) {
            postcodeInputLayout.setError(getString(validation.getPostcodeError()));
        } else {
            postcodeInputLayout.setError("");
        }

        postcodeEditText.setInputType(validation.isPostcodeNumeric() ?
                InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        postcodeEditText.setPrivateImeOptions("nm"); // prevent text suggestions in keyboard
        postcodeEditText.setSelection(postcodeEditText.getText().length());

        cardsAcceptedErrorText.setVisibility(validation.isCountryValid() ? View.GONE : View.VISIBLE);
        postcodeInputLayout.setVisibility(validation.isCountryValid() ? View.VISIBLE : View.INVISIBLE);
    }

    private void showExpiryDateErrors(PaymentFormValidation formView) {
        expiryDateEntryView.setError(formView.getExpiryDateError(), formView.isShowExpiryDateError());
    }

    private void showCardNumberErrors(CardNumberValidation cardNumberValidation) {
        cardNumberEntryView.setError(cardNumberValidation.getError(), cardNumberValidation.isShowError());
        cardNumberEntryView.setMaxLength(cardNumberValidation.getMaxLength());
    }

    private void moveFieldFocus(PaymentFormValidation formView) {
        if (cardNumberEntryView.hasFocus() && formView.getCardNumberValidation().isEntryComplete() && !formView.getCardNumberValidation().isShowError()) {
            if (startDateAndIssueNumberContainer.getVisibility() == View.VISIBLE) {
                startDateEditText.requestFocus();
            } else {
                expiryDateEntryView.requestFocus();
            }
        } else if (expiryDateEntryView.hasFocus() && formView.isExpiryDateEntryComplete() && !formView.isShowExpiryDateError()) {
            cvvEditText.requestFocus();
        } else if (cvvEditText.hasFocus() && formView.isCvvValid()) {
            if (countryAndPostcodeContainer.getVisibility() == View.VISIBLE) {
                postcodeEditText.requestFocus();
            }
        } else if (startDateEditText.hasFocus()
                && formView.getStartDateAndIssueNumberState().isStartDateEntryComplete()
                && !formView.getStartDateAndIssueNumberState().isShowStartDateError()) {
            issueNumberEditText.requestFocus();
        }
    }

    private Country getCountry() {
        return countrySpinner.getSelectedCountry();
    }

    private void submitForm() {
        Card.Builder cardBuilder = new Card.Builder()
                .setCardNumber(cardNumberEntryView.getText())
                .setExpiryDate(expiryDateEntryView.getText())
                .setCvv(getCvv());

        Address.Builder addressBuilder = new Address.Builder()
                .setPostCode(getPostcode());

        if (isAvsEnabled()) {
            addressBuilder.setCountryCode(countrySpinner.getSelectedCountry().getCode());
        }

        cardBuilder.setCardAddress(addressBuilder.build());

        if (cardNumberEntryView.getCardType() == CardType.MAESTRO) {
            cardBuilder.setIssueNumber(getIssueNumber())
                    .setStartDate(trim(startDateEditText));
        }

        if (cardEntryListener != null) {
            cardEntryListener.onSubmit(cardBuilder.build());
        }
    }

    private String getIssueNumber() {
        return trim(issueNumberEditText);
    }

    private String getPostcode() {
        return trim(postcodeEditText);
    }

    private String trim(EditText editText) {
        return editText.getText().toString().trim();
    }

    private String getCvv() {
        return trim(cvvEditText);
    }

    public static CardEntryFragment newInstance(JudoOptions judoOptions, CardEntryListener listener) {
        CardEntryFragment cardEntryFragment = new CardEntryFragment();
        cardEntryFragment.setCardEntryListener(listener);

        Bundle arguments = new Bundle();
        arguments.putParcelable(Judo.JUDO_OPTIONS, judoOptions);
        cardEntryFragment.setArguments(arguments);

        return cardEntryFragment;
    }

    public static CardEntryFragment newInstance(CardEntryListener paymentListener) {
        CardEntryFragment cardEntryFragment = new CardEntryFragment();
        cardEntryFragment.setCardEntryListener(paymentListener);

        return cardEntryFragment;
    }

    public void setCardEntryListener(CardEntryListener cardEntryListener) {
        this.cardEntryListener = cardEntryListener;
    }

}