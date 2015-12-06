package com.judopay.payment.form;

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
import com.judopay.JudoPay;
import com.judopay.PaymentForm;
import com.judopay.PaymentFormValidation;
import com.judopay.R;
import com.judopay.StartDateAndIssueNumberValidation;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.CardType;
import com.judopay.model.Country;
import com.judopay.view.CardNumberFormattingTextWatcher;
import com.judopay.view.CardTypeImageView;
import com.judopay.view.CompositeOnFocusChangeListener;
import com.judopay.view.CountrySpinner;
import com.judopay.view.CvvImageView;
import com.judopay.view.DateSeparatorTextWatcher;
import com.judopay.view.EmptyTextHintOnFocusChangeListener;
import com.judopay.view.HidingViewTextWatcher;
import com.judopay.view.HintFocusListener;
import com.judopay.view.ScrollOnFocusChangeListener;
import com.judopay.view.SimpleTextWatcher;
import com.judopay.view.SingleClickOnClickListener;

import static com.judopay.JudoPay.isAvsEnabled;

public final class PaymentFormFragment extends Fragment {

    public static final String KEY_PAYMENT_FORM_OPTIONS = "Judo-PaymentFormOptions";

    private EditText cvvEditText;
    private View cvvHelperText;
    private Button paymentButton;
    private CardTypeImageView cardTypeImageView;
    private CvvImageView cvvImageView;
    private CountrySpinner countrySpinner;
    private EditText postcodeEditText;

    private EditText startDateEditText;
    private EditText expiryDateEditText;
    private EditText cardNumberEditText;
    private View cardNumberHelperText;
    private EditText issueNumberEditText;
    private TextInputLayout cvvInputLayout;
    private TextInputLayout cardNumberInputLayout;
    private TextInputLayout expiryDateInputLayout;
    private TextInputLayout startDateInputLayout;
    private TextInputLayout postcodeInputLayout;
    private View startDateAndIssueNumberContainer;
    private View countryAndPostcodeContainer;
    private HintFocusListener cvvHintChangeListener;
    private ScrollView scrollView;
    private View cardsAcceptedErrorText;
    private View issueNumberHelperText;

    private PaymentFormOptions paymentFormOptions;
    private PaymentFormListener paymentFormListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_form, container, false);

        paymentButton = (Button) view.findViewById(R.id.payment_button);

        cvvEditText = (EditText) view.findViewById(R.id.cvv_edit_text);
        cvvInputLayout = (TextInputLayout) view.findViewById(R.id.cvv_input_layout);
        cvvHelperText = view.findViewById(R.id.cvv_helper_text);

        postcodeEditText = (EditText) view.findViewById(R.id.post_code_edit_text);
        postcodeInputLayout = (TextInputLayout) view.findViewById(R.id.post_code_input_layout);

        cardNumberEditText = (EditText) view.findViewById(R.id.card_number_edit_text);
        cardNumberInputLayout = (TextInputLayout) view.findViewById(R.id.card_number_input_layout);
        cardNumberHelperText = view.findViewById(R.id.card_number_helper_text);

        expiryDateEditText = (EditText) view.findViewById(R.id.expiry_date_edit_text);
        expiryDateInputLayout = (TextInputLayout) view.findViewById(R.id.expiry_date_input_layout);

        startDateEditText = (EditText) view.findViewById(R.id.start_date_edit_text);
        startDateInputLayout = (TextInputLayout) view.findViewById(R.id.start_date_input_layout);

        countrySpinner = (CountrySpinner) view.findViewById(R.id.country_spinner);

        cardTypeImageView = (CardTypeImageView) view.findViewById(R.id.card_type_view);
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
            protected void onTextChanged(CharSequence text) {
                updateFormView();
            }
        };

        if (getArguments() != null && getArguments().containsKey(KEY_PAYMENT_FORM_OPTIONS)) {
            this.paymentFormOptions = getArguments().getParcelable(KEY_PAYMENT_FORM_OPTIONS);

            if (paymentFormOptions != null) {
                if (paymentFormOptions.getButtonLabel() != null) {
                    this.paymentButton.setText(paymentFormOptions.getButtonLabel());
                }

                if (paymentFormOptions.getCardToken() != null) {
                    cardTypeImageView.setCardType(paymentFormOptions.getCardToken().getType());
                    cvvEditText.requestFocus();
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
            public void doClick(View v) {
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
        if (paymentFormOptions.getCardToken() == null) {
            expiryDateEditText.setOnFocusChangeListener(new HintFocusListener(expiryDateEditText, R.string.date_hint));

            expiryDateEditText.addTextChangedListener(formValidator);
            expiryDateEditText.addTextChangedListener(new DateSeparatorTextWatcher(expiryDateEditText));
        } else {
            expiryDateEditText.setEnabled(false);
            expiryDateEditText.setText(getString(R.string.token_date));
        }
    }

    private void initialiseCardNumber(SimpleTextWatcher formValidator) {
        if (paymentFormOptions.getCardToken() == null) {
            cardNumberEditText.setOnFocusChangeListener(new CompositeOnFocusChangeListener(
                    new EmptyTextHintOnFocusChangeListener(cardNumberHelperText),
                    new HintFocusListener(cardNumberEditText, R.string.card_number_hint)
            ));

            cardNumberEditText.addTextChangedListener(formValidator);
            cardNumberEditText.addTextChangedListener(new CardNumberFormattingTextWatcher());
            cardNumberEditText.addTextChangedListener(new HidingViewTextWatcher(cardNumberHelperText));
        } else {
            cardNumberEditText.setEnabled(false);
            cardNumberEditText.setText(getString(R.string.token_card_number, paymentFormOptions.getCardToken().getLastFour()));
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
                .setCardNumber(getCardNumber())
                .setCvv(getCvv())
                .setCountry(getCountry())
                .setPostcode(getPostcode())
                .setIssueNumber(getIssueNumber())
                .setExpiryDate(trim(expiryDateEditText))
                .setStartDate(trim(startDateEditText))
                .setAddressRequired(JudoPay.isAvsEnabled())
                .setAmexSupported(JudoPay.isAmexEnabled())
                .setMaestroSupported(JudoPay.isMaestroEnabled());

        CardToken cardToken = paymentFormOptions.getCardToken();

        if (cardToken != null) {
            builder.setTokenCard(true)
                    .setCardType(cardToken.getType());
        }

        PaymentFormValidation formView = new PaymentFormValidation.Builder()
                .build(builder.build());

        if (cardToken == null) {
            cardTypeImageView.setCardType(formView.getCardType());
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
        expiryDateInputLayout.setErrorEnabled(formView.isShowExpiryDateError());
        if (formView.isShowExpiryDateError()) {
            expiryDateInputLayout.setError(getString(formView.getExpiryDateError()));
        } else {
            expiryDateInputLayout.setError("");
        }
    }

    private void showCardNumberErrors(CardNumberValidation cardNumberValidation) {
        cardNumberInputLayout.setErrorEnabled(cardNumberValidation.isShowError());

        if (cardNumberValidation.isShowError()) {
            cardNumberInputLayout.setError(getString(cardNumberValidation.getError()));
        } else {
            cardNumberInputLayout.setError("");
        }

        cardNumberEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cardNumberValidation.getMaxLength())});
    }

    private void moveFieldFocus(PaymentFormValidation formView) {
        if (cardNumberEditText.hasFocus() && formView.getCardNumberValidation().isEntryComplete() && !formView.getCardNumberValidation().isShowError()) {
            if (startDateAndIssueNumberContainer.getVisibility() == View.VISIBLE) {
                startDateEditText.requestFocus();
            } else {
                expiryDateEditText.requestFocus();
            }
        } else if (expiryDateEditText.hasFocus() && formView.isExpiryDateEntryComplete() && !formView.isShowExpiryDateError()) {
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
                .setCardNumber(getCardNumber())
                .setExpiryDate(trim(expiryDateEditText))
                .setCvv(getCvv());

        Address.Builder addressBuilder = new Address.Builder()
                .setPostCode(getPostcode());

        if (isAvsEnabled()) {
            addressBuilder.setCountryCode(countrySpinner.getSelectedCountry().getCode());
        }

        cardBuilder.setCardAddress(addressBuilder.build());

        if (CardType.matchCardNumber(cardNumberEditText.getText().toString()) == CardType.MAESTRO) {
            cardBuilder.setIssueNumber(getIssueNumber())
                    .setStartDate(trim(startDateEditText));
        }

        if (paymentFormListener != null) {
            paymentFormListener.onSubmit(cardBuilder.build());
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

    private String getCardNumber() {
        return trim(cardNumberEditText).replaceAll(" ", "");
    }

    public static PaymentFormFragment newInstance(PaymentFormOptions paymentFormOptions, PaymentFormListener listener) {
        PaymentFormFragment paymentFormFragment = new PaymentFormFragment();
        paymentFormFragment.setPaymentFormListener(listener);

        Bundle arguments = new Bundle();
        arguments.putParcelable(PaymentFormFragment.KEY_PAYMENT_FORM_OPTIONS, paymentFormOptions);
        paymentFormFragment.setArguments(arguments);

        return paymentFormFragment;
    }

    public static PaymentFormFragment newInstance(PaymentFormListener paymentListener) {
        PaymentFormFragment paymentFormFragment = new PaymentFormFragment();
        paymentFormFragment.setPaymentFormListener(paymentListener);

        return paymentFormFragment;
    }

    public void setPaymentFormListener(PaymentFormListener paymentFormListener) {
        this.paymentFormListener = paymentFormListener;
    }

}