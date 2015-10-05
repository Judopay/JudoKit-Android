package com.judopay.payment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.customer.Card;
import com.judopay.customer.CardAddress;
import com.judopay.customer.CardDate;
import com.neovisionaries.i18n.CountryCode;

import java.util.ArrayList;
import java.util.Locale;

import static android.view.View.VISIBLE;

public class CardFormFragment extends Fragment {

    private static final String JUDO_PAYMENT = "Judo-Payment";

    private Button paymentButton;
    private EditText cvvEditText;
    private EditText postcodeEditText;

    private Spinner countrySpinner;
    private EditText cardNumberEditText;
    private CardDateEditText expiryDateEditText;

    private PaymentFormListener paymentFormListener;
    private CountrySpinnerAdapter countryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_payment, container, false);

        paymentButton = (Button) view.findViewById(R.id.payment_button);
        cvvEditText = (EditText) view.findViewById(R.id.cvv_edit_text);
        postcodeEditText = (EditText) view.findViewById(R.id.post_code_edit_text);
        cardNumberEditText = (EditText) view.findViewById(R.id.card_number_edit_text);
        expiryDateEditText = (CardDateEditText) view.findViewById(R.id.expiry_date_edit_text);
        countrySpinner = (Spinner) view.findViewById(R.id.country_spinner);

        initialiseView();

        return view;
    }

    private void initialiseView() {
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expiryDateEditText.isValid()) {
                    attemptPayment();
                } else {
                    expiryDateEditText.setError("Invalid expiry date");
                }
            }
        });

        if (JudoPay.isAvsEnabled()) {
            initialiseCountrySpinner();
        }
    }

    private void initialiseCountrySpinner() {
        countrySpinner.setVisibility(VISIBLE);
        ArrayList<Locale> list = new ArrayList<>();
        list.add(Locale.UK);
        list.add(Locale.CANADA);
        list.add(Locale.US);

        this.countryAdapter = new CountrySpinnerAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        countrySpinner.setAdapter(countryAdapter);
    }

    private void attemptPayment() {
        CardAddress.Builder cardAddressBuilder = new CardAddress.Builder();

        if (JudoPay.isAvsEnabled()) {
            Locale locale = countryAdapter.getItem(countrySpinner.getSelectedItemPosition());
            cardAddressBuilder.setPostcode(getPostcode())
                    .setCountryCode(CountryCode.getByCode(locale.getCountry()).getNumeric());
        }

        Card card = new Card(getCardNumber(),
                cardAddressBuilder.build(),
                null,
                new CardDate(getExpiryDate()),
                getCvv());

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

    private String getPostcode() {
        return trim(postcodeEditText);
    }

    private String trim(EditText editText) {
        return editText.getText().toString().trim();
    }

    private String getCvv() {
        return trim(cvvEditText);
    }

    private String getExpiryDate() {
        return trim(expiryDateEditText);
    }

    private String getCardNumber() {
        return trim(cardNumberEditText);
    }

    public static Fragment newInstance(Parcelable payment, PaymentFormListener paymentListener) {
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
}