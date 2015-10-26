package com.judopay.payment.form;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.judopay.HintFocusListener;
import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.customer.Card;
import com.judopay.customer.CardAddress;
import com.judopay.customer.CardDate;
import com.judopay.customer.CardType;
import com.judopay.customer.Country;
import com.judopay.payment.PaymentFormListener;
import com.judopay.payment.ScrollHintFocusListener;
import com.judopay.payment.SingleClickOnClickListener;
import com.judopay.payment.form.address.CountryAndPostcodeValidation;
import com.judopay.payment.form.address.CountrySpinner;
import com.judopay.payment.form.address.PostcodeEditText;
import com.judopay.payment.form.cardnumber.CardNumberFormattingTextWatcher;
import com.judopay.payment.form.cardnumber.CardNumberValidation;
import com.judopay.payment.form.cvv.CvvImageView;
import com.judopay.payment.form.date.DateSeparatorTextWatcher;

import static com.judopay.JudoPay.isAvsEnabled;

public class PaymentFormFragment extends Fragment {

    private static final String JUDO_PAYMENT = "Judo-Payment";

    private EditText cvvEditText;
    private Button paymentButton;
    private CardTypeImageView cardTypeImageView;
    private CvvImageView cvvImageView;
    private CountrySpinner countrySpinner;
    private PostcodeEditText postcodeEditText;
    private EditText startDateEditText;
    private EditText expiryDateEditText;
    private EditText cardNumberEditText;
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

    private PaymentFormListener paymentFormListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_form, container, false);

        paymentButton = (Button) view.findViewById(R.id.payment_button);
        cvvEditText = (EditText) view.findViewById(R.id.cvv_edit_text);
        cvvInputLayout = (TextInputLayout) view.findViewById(R.id.cvv_input_layout);

        postcodeEditText = (PostcodeEditText) view.findViewById(R.id.post_code_edit_text);
        postcodeInputLayout = (TextInputLayout) view.findViewById(R.id.post_code_input_layout);

        cardNumberEditText = (EditText) view.findViewById(R.id.card_number_edit_text);
        cardNumberInputLayout = (TextInputLayout) view.findViewById(R.id.card_number_input_layout);

        expiryDateEditText = (EditText) view.findViewById(R.id.expiry_date_edit_text);
        expiryDateInputLayout = (TextInputLayout) view.findViewById(R.id.expiry_date_input_layout);

        startDateEditText = (EditText) view.findViewById(R.id.start_date_edit_text);
        startDateInputLayout = (TextInputLayout) view.findViewById(R.id.start_date_input_layout);

        countrySpinner = (CountrySpinner) view.findViewById(R.id.country_spinner);

        cardTypeImageView = (CardTypeImageView) view.findViewById(R.id.card_type_view);
        cvvImageView = (CvvImageView) view.findViewById(R.id.cvv_image_view);

        issueNumberEditText = (EditText) view.findViewById(R.id.issue_number_edit_text);

        startDateAndIssueNumberContainer = view.findViewById(R.id.start_date_issue_number_container);
        countryAndPostcodeContainer = view.findViewById(R.id.country_postcode_container);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleTextWatcher formValidator = new SimpleTextWatcher() {
            @Override
            protected void onTextChanged(CharSequence text) {
                updateFormView();
            }
        };

        cardNumberEditText.setOnFocusChangeListener(new HintFocusListener(cardNumberEditText, R.string.card_number_hint));
        cardNumberEditText.addTextChangedListener(formValidator);
        cardNumberEditText.addTextChangedListener(new CardNumberFormattingTextWatcher());

        expiryDateEditText.setOnFocusChangeListener(new HintFocusListener(expiryDateEditText, R.string.date_hint));
        expiryDateEditText.addTextChangedListener(formValidator);
        expiryDateEditText.addTextChangedListener(new DateSeparatorTextWatcher(expiryDateEditText));

        startDateEditText.setOnFocusChangeListener(new HintFocusListener(startDateEditText, R.string.date_hint));
        startDateEditText.addTextChangedListener(formValidator);
        startDateEditText.addTextChangedListener(new DateSeparatorTextWatcher(startDateEditText));

        cvvHintChangeListener = new HintFocusListener(cvvEditText, R.string.cvv_hint);

        cvvEditText.setOnFocusChangeListener(cvvHintChangeListener);
        cvvEditText.addTextChangedListener(formValidator);

        issueNumberEditText.setOnFocusChangeListener(new HintFocusListener(issueNumberEditText, R.string.issue_number_hint));
        issueNumberEditText.addTextChangedListener(formValidator);

        postcodeEditText.setOnFocusChangeListener(new ScrollHintFocusListener(postcodeEditText, scrollView, R.string.empty));
        postcodeEditText.addTextChangedListener(formValidator);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFormView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        paymentButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick(View v) {
                hideKeyboard();
                submitForm();
            }
        });
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
                .setExpiryDate(trim(expiryDateEditText))
                .setCountry(getCountry())
                .setPostcode(getPostcode())
                .setIssueNumber(getIssueNumber())
                .setStartDate(trim(startDateEditText))
                .setAddressRequired(JudoPay.isAvsEnabled())
                .setAmexSupported(JudoPay.isAmexEnabled())
                .setMaestroSupported(JudoPay.isMaestroEnabled());

        PaymentFormValidation formView = new PaymentFormValidation.Builder()
                .build(builder.build());

        cardTypeImageView.setCardType(formView.getCardType());

        updateFormErrors(formView);
        moveFieldFocus(formView);
    }

    private void updateFormErrors(PaymentFormValidation formView) {
        showCardNumberErrors(formView.getCardNumberValidation());

        showExpiryDateErrors(formView);

        showStartDateAndIssueNumberErrors(formView.getStartDateAndIssueNumberState());

        cvvInputLayout.setHint(getString(formView.getCvvLabel()));
        cvvHintChangeListener.setHintResourceId(formView.getCvvHint());

        cvvEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(formView.getCvvLength())});

        cvvImageView.setCardType(formView.getCardType());

        updateCountryAndPostcode(formView.getCountryAndPostcodeValidation());

        paymentButton.setVisibility(formView.isPaymentButtonEnabled() ? View.VISIBLE : View.GONE);
    }

    private void showStartDateAndIssueNumberErrors(StartDateAndIssueNumberValidation startDateAndIssueNumberValidation) {
        startDateInputLayout.setErrorEnabled(startDateAndIssueNumberValidation.isShowStartDateError());

        if(startDateAndIssueNumberValidation.isShowStartDateError()) {
            startDateInputLayout.setError(getString(startDateAndIssueNumberValidation.getStartDateError()));
        } else {
            startDateInputLayout.setError("");
        }

        startDateAndIssueNumberContainer.setVisibility(startDateAndIssueNumberValidation.isIssueNumberAndStartDateRequired() ? View.VISIBLE : View.GONE);
    }

    private void updateCountryAndPostcode(CountryAndPostcodeValidation countryAndPostcodeValidation) {
        countryAndPostcodeContainer.setVisibility(countryAndPostcodeValidation.isCountryAndPostcodeRequired() ? View.VISIBLE : View.GONE);

        postcodeInputLayout.setErrorEnabled(countryAndPostcodeValidation.isShowPostcodeError());
        postcodeInputLayout.setHint(getString(countryAndPostcodeValidation.getPostcodeLabel()));

        if(countryAndPostcodeValidation.isShowPostcodeError()) {
            postcodeInputLayout.setError(getString(countryAndPostcodeValidation.getPostcodeError()));
        } else {
            postcodeInputLayout.setError("");
        }
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
            expiryDateEditText.requestFocus();
        } else if (expiryDateEditText.hasFocus() && formView.isExpiryDateEntryComplete() && !formView.isShowExpiryDateError()) {
            cvvEditText.requestFocus();
        } else if (cvvEditText.hasFocus() && formView.isCvvValid()) {
            if (startDateAndIssueNumberContainer.getVisibility() == View.VISIBLE) {
                startDateEditText.requestFocus();
            } else if(countryAndPostcodeContainer.getVisibility() == View.VISIBLE) {
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
                .setExpiryDate(getExpiryDate())
                .setCvv(getCvv());

        CardAddress.Builder cardAddressBuilder = new CardAddress.Builder()
                .setPostcode(getPostcode());

        if (isAvsEnabled()) {
            cardAddressBuilder.setCountryCode(countrySpinner.getSelectedCountry().getCode());
        }

        cardBuilder.setCardAddress(cardAddressBuilder.build());

        if (CardType.matchCardNumber(cardNumberEditText.getText().toString()) == CardType.MAESTRO) {
            cardBuilder.setIssueNumber(getIssueNumber())
                    .setStartDate(getStartDate());
        }

        if (paymentFormListener != null) {
            paymentFormListener.onSubmit(cardBuilder.build());
        }
    }

    private CardDate getStartDate() {
        return new CardDate(trim(startDateEditText));
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

    private CardDate getExpiryDate() {
        return new CardDate(trim(expiryDateEditText));
    }

    private String getCardNumber() {
        return trim(cardNumberEditText).replaceAll(" ", "");
    }

    public static PaymentFormFragment newInstance(Parcelable payment, PaymentFormListener paymentListener) {
        PaymentFormFragment paymentFormFragment = new PaymentFormFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(JUDO_PAYMENT, payment);
        paymentFormFragment.setArguments(arguments);
        paymentFormFragment.setPaymentFormListener(paymentListener);

        return paymentFormFragment;
    }

    public void setPaymentFormListener(PaymentFormListener paymentFormListener) {
        this.paymentFormListener = paymentFormListener;
    }

}