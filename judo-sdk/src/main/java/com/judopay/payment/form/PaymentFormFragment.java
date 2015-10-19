package com.judopay.payment.form;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.judopay.HintChangingFocusListener;
import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.customer.Card;
import com.judopay.customer.CardAddress;
import com.judopay.customer.CardDate;
import com.judopay.customer.CardType;
import com.judopay.payment.PaymentFormListener;
import com.judopay.payment.form.date.DateEditText;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.judopay.JudoPay.isAvsEnabled;

public class PaymentFormFragment extends Fragment {

    private static final String JUDO_PAYMENT = "Judo-Payment";

    private Button paymentButton;
    private EditText cvvEditText;

    private CardTypeView cardTypeView;
    private CountrySpinner countrySpinner;
    private PostcodeEditText postcodeEditText;
    private DateEditText startDateEditText;
    private DateEditText expiryDateEditText;
    private CardNumberEditText cardNumberEditText;
    private IssueNumberEditText issueNumberEditText;

    private PaymentFormListener paymentFormListener;
    private TextInputLayout cardNumberInputLayout;
    private TextInputLayout expiryDateInputLayout;
    private TextInputLayout postcodeInputLayout;
    private TextInputLayout startDateInputLayout;
    private TextInputLayout cvvInputLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_payment, container, false);

        paymentButton = (Button) view.findViewById(R.id.payment_button);

        cvvEditText = (EditText) view.findViewById(R.id.cvv_edit_text);
        cvvInputLayout = (TextInputLayout) view.findViewById(R.id.cvv_input_layout);

        postcodeEditText = (PostcodeEditText) view.findViewById(R.id.post_code_edit_text);
        postcodeInputLayout = (TextInputLayout) view.findViewById(R.id.post_code_input_layout);

        cardNumberEditText = (CardNumberEditText) view.findViewById(R.id.card_number_edit_text);
        cardNumberInputLayout = (TextInputLayout) view.findViewById(R.id.card_number_input_layout);

        expiryDateEditText = (DateEditText) view.findViewById(R.id.expiry_date_edit_text);
        expiryDateInputLayout = (TextInputLayout) view.findViewById(R.id.expiry_date_input_layout);

        countrySpinner = (CountrySpinner) view.findViewById(R.id.country_spinner);

        cardTypeView = (CardTypeView) view.findViewById(R.id.card_type_view);

        startDateEditText = (DateEditText) view.findViewById(R.id.start_date_edit_text);
        startDateInputLayout = (TextInputLayout) view.findViewById(R.id.start_date_input_layout);

        issueNumberEditText = (IssueNumberEditText) view.findViewById(R.id.issue_number_edit_text);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardNumberInputLayout.setHintAnimationEnabled(false);
        cardNumberEditText.setOnFocusChangeListener(new HintChangingFocusListener(cardNumberEditText, R.string.card_number_hint));

        expiryDateInputLayout.setHintAnimationEnabled(false);
        expiryDateEditText.setOnFocusChangeListener(new HintChangingFocusListener(expiryDateEditText, R.string.date_hint));

        postcodeInputLayout.setHintAnimationEnabled(false);
        postcodeEditText.setOnFocusChangeListener(new HintChangingFocusListener(postcodeEditText, R.string.empty));

        cvvInputLayout.setHintAnimationEnabled(false);
        cvvEditText.setOnFocusChangeListener(new HintChangingFocusListener(cvvEditText, R.string.cvv_hint));

        startDateInputLayout.setHintAnimationEnabled(false);
        startDateEditText.setOnFocusChangeListener(new HintChangingFocusListener(startDateEditText, R.string.date_hint));

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getSimpleName(), "Payment button pressed!");
                if (formValid()) {
                    submitForm();
                }
            }
        });

        cardNumberEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onTextChanged() {
                if (cardNumberEditText.isValid()) {
                    cardNumberInputLayout.setErrorEnabled(false);
                    cardNumberInputLayout.setError(null);
                } else {
                    cardNumberInputLayout.setErrorEnabled(true);
                    cardNumberInputLayout.setError("Card not valid");
                }
            }
        });

        expiryDateEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onTextChanged() {
                if (expiryDateEditText.isValid()) {
                    expiryDateInputLayout.setErrorEnabled(false);
                    expiryDateInputLayout.setError(null);
                } else {
                    expiryDateInputLayout.setError("Please check date");
                    cardNumberInputLayout.setErrorEnabled(true);
                }
            }
        });

        startDateEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onTextChanged() {
                if (startDateEditText.isValid()) {
                    startDateInputLayout.setErrorEnabled(false);
                    startDateInputLayout.setError(null);
                } else {
                    startDateInputLayout.setErrorEnabled(false);
                    startDateInputLayout.setError("Please check date");
                }
            }
        });

//        countrySpinner.setOnItemSelectedListener(this);

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

        if (isAvsEnabled()) {
            initialiseCountrySpinner();
        }
    }

    private boolean formValid() {
        return expiryDateEditText.isValid()
                && cardNumberEditText.isValid()
                && isCardTypeAllowed()
                && isMandatoryCardTypeFieldsValid();
    }

    private boolean isCardTypeAllowed() {
        return (!cardNumberEditText.isMaestroEntered() || JudoPay.isMaestroEnabled())
                && (!cardNumberEditText.isAmexEntered() || JudoPay.isAmexEnabled());
    }

    private boolean isMandatoryCardTypeFieldsValid() {
        return isAvsRequiredFieldsValid() && isMaestroRequiredFieldsValid() && isCvvValid();
    }

    private boolean isCvvValid() {
        return cardNumberEditText.isAmexEntered() && Card.isCidvValid(getCvv()) || Card.isCv2Valid(getCvv());
    }

    private boolean isAvsRequiredFieldsValid() {
        return !isAvsEnabled() || isAvsEnabled() && postcodeEditText.isValid() && countrySpinner.isCountrySelected();
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

        if (cardNumberEditText.isMaestroEntered()) {
            cardBuilder.setIssueNumber(getIssueNumber())
                    .setStartDate(getStartDate());
        }

        Card card = cardBuilder.build();
        boolean errors = false;

//        if (!card.isLuhnValid()) {
//            errors = true;
//            cardNumberInputLayout.setError("Invalid card no.");
//        }

        if (!card.isExpiryDateValid()) {
            errors = true;
            expiryDateInputLayout.setError("Invalid expiry date");
        }

        if (!card.isCvvValid()) {
            errors = true;
            cvvInputLayout.setError("Invalid CV2");
        }

        if (!errors && paymentFormListener != null) {
            paymentFormListener.onSubmit(card);
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