package com.judopay.payment.form;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.customer.Card;
import com.judopay.customer.CardAddress;
import com.judopay.customer.CardDate;
import com.judopay.customer.CardType;
import com.judopay.customer.Country;
import com.judopay.payment.PaymentFormListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CardFormFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener {

    private static final String JUDO_PAYMENT = "Judo-Payment";

    private Button paymentButton;
    private EditText cvvEditText;

    private CardTypeView cardTypeView;
    private CountrySpinner countrySpinner;
    private PostcodeEditText postcodeEditText;
    private CardDateEditText startDateEditText;
    private CardDateEditText expiryDateEditText;
    private CardNumberEditText cardNumberEditText;
    private IssueNumberEditText issueNumberEditText;

    private PaymentFormListener paymentFormListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_payment, container, false);

        paymentButton = (Button) view.findViewById(R.id.payment_button);
        cvvEditText = (EditText) view.findViewById(R.id.cvv_edit_text);
        postcodeEditText = (PostcodeEditText) view.findViewById(R.id.post_code_edit_text);
        cardNumberEditText = (CardNumberEditText) view.findViewById(R.id.card_number_edit_text);
        expiryDateEditText = (CardDateEditText) view.findViewById(R.id.expiry_date_edit_text);
        countrySpinner = (CountrySpinner) view.findViewById(R.id.country_spinner);
        cardTypeView = (CardTypeView) view.findViewById(R.id.card_type_view);
        startDateEditText = (CardDateEditText) view.findViewById(R.id.start_date_edit_text);
        issueNumberEditText = (IssueNumberEditText) view.findViewById(R.id.issue_number_edit_text);

        initialiseView();

        return view;
    }

    private void initialiseView() {
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValid()) {
                    attemptPayment();
                }
            }
        });

        addTextChangeListeners(cvvEditText, postcodeEditText, cardNumberEditText,
                expiryDateEditText, startDateEditText, issueNumberEditText);

        countrySpinner.setOnItemSelectedListener(this);

        cardNumberEditText.setCardListener(new CardNumberEditText.CardListener() {
            @Override
            public void onCardTypeChanged(int cardType) {
                cardTypeView.setCardType(cardType);

                if (CardType.MAESTRO == cardType) {
                    showAdditionalFields();
                } else {
                    hideAdditionalFields();
                }
            }
        });

        if (JudoPay.isAvsEnabled()) {
            initialiseCountrySpinner();
        }
    }

    private void addTextChangeListeners(EditText... editTexts) {
        for(EditText editText : editTexts) {
            editText.addTextChangedListener(this);
        }
    }

    private boolean formValid() {
        return expiryDateEditText.isValid()
                && cardNumberEditText.isValid()
                && isMandatoryCardTypeFieldsValid();
    }

    private boolean isMandatoryCardTypeFieldsValid() {
        return isAvsRequiredFieldsValid() && isMaestroRequiredFieldsValid() && isCvvValid();
    }

    private boolean isCvvValid() {
        return cardNumberEditText.isAmexEntered() && Card.isCidvValid(getCvv()) || Card.isCv2Valid(getCvv());
    }

    private boolean isAvsRequiredFieldsValid() {
        return !JudoPay.isAvsEnabled() || JudoPay.isAvsEnabled() && postcodeEditText.isValid() && countrySpinner.isCountrySelected();
    }

    private boolean isMaestroRequiredFieldsValid() {
        return !cardNumberEditText.isMaestroEntered() || issueNumberEditText.isValid() && startDateEditText.isValid();
    }

    private void hideAdditionalFields() {
        issueNumberEditText.setVisibility(GONE);
        startDateEditText.setVisibility(GONE);
    }

    private void showAdditionalFields() {
        issueNumberEditText.setVisibility(VISIBLE);
        startDateEditText.setVisibility(VISIBLE);
    }

    private void initialiseCountrySpinner() {
        countrySpinner.setVisibility(VISIBLE);
    }

    private void attemptPayment() {
        Card.Builder cardBuilder = new Card.Builder()
                .setCardNumber(getCardNumber())
                .setExpiryDate(getExpiryDate())
                .setCvv(getCvv());

        if (JudoPay.isAvsEnabled()) {
            Country country = countrySpinner.getSelectedCountry();
            cardBuilder.setCardAddress(new CardAddress.Builder()
                    .setPostcode(getPostcode())
                    .setCountryCode(country.getCode())
                    .build());
        }

        if (cardNumberEditText.isMaestroEntered()) {
            cardBuilder.setIssueNumber(getIssueNumber())
                    .setStartDate(getStartDate());
        }

        Card card = cardBuilder.build();
        boolean errors = false;

        if (!card.isLuhnValid()) {
            errors = true;
            cardNumberEditText.setError("Invalid card no.");
        }

        if (!card.isExpiryDateValid()) {
            errors = true;
            expiryDateEditText.setError("Invalid expiry date");
        }

        if (!card.isCvvValid()) {
            errors = true;
            cvvEditText.setError("Invalid CV2");
        }

        if (!errors && paymentFormListener != null) {
            paymentFormListener.onSubmitCard(card);
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
        return trim(cardNumberEditText);
    }

    public static CardFormFragment newInstance(Parcelable payment, PaymentFormListener paymentListener) {
        CardFormFragment cardFormFragment = new CardFormFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(JUDO_PAYMENT, payment);
        cardFormFragment.setArguments(arguments);
        cardFormFragment.setPaymentFormListener(paymentListener);

        return cardFormFragment;
    }

    public void setPaymentFormListener(PaymentFormListener paymentFormListener) {
        this.paymentFormListener = paymentFormListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        paymentButton.setEnabled(formValid());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        paymentButton.setEnabled(formValid());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}