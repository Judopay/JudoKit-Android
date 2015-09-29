package com.judopay.payment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.judopay.Client;
import com.judopay.R;
import com.judopay.customer.Address;
import com.judopay.customer.Location;

import retrofit.Callback;
import retrofit.Response;

public class CardPaymentFragment extends Fragment {

    private static final String JUDO_PAYMENT = "Judo-Payment";

    private PaymentListener listener;

    private Button paymentButton;
    private EditText cv2EditText;
    private EditText postcodeEditText;
    private EditText cardNumberEditText;
    private EditText expiryDateEditText;

    private PaymentService paymentService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_payment, container, false);

        paymentButton = (Button) view.findViewById(R.id.payment_button);
        cv2EditText = (EditText) view.findViewById(R.id.cv2_edit_text);
        postcodeEditText = (EditText) view.findViewById(R.id.post_code_edit_text);
        cardNumberEditText = (EditText) view.findViewById(R.id.card_number_edit_text);
        expiryDateEditText = (EditText) view.findViewById(R.id.expiry_date_edit_text);

        this.paymentService = new PaymentService();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performPayment();
            }
        });
    }

    private void performPayment() {
        Payment payment = getArguments().getParcelable(JUDO_PAYMENT);

        Transaction.Builder builder = new Transaction.Builder()
                .setAmount(String.valueOf(payment.getAmount()))
                .setCardAddress(new Address.Builder()
                        .setPostCode(postcodeEditText.getText().toString())
                        .build())
                .setClientDetails(new Client())
                .setConsumerLocation(new Location())
                .setCardNumber(cardNumberEditText.getText().toString())
                .setCurrency(payment.getCurrency())
                .setCv2(cv2EditText.getText().toString())
                .setJudoId(payment.getJudoId())
                .setYourConsumerReference("consumerRef")
                .setYourPaymentReference(payment.getPaymentRef())
                .setExpiryDate(expiryDateEditText.getText().toString());

        paymentService.payment(builder.build(), new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Response<PaymentResponse> response) {
//                        Toast.makeText(MainActivity.this, "Payment response success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
//                Toast.makeText(MainActivity.this, "Payment response error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Fragment newInstance(Parcelable payment, PaymentListener paymentListener) {
        CardPaymentFragment cardPaymentFragment = new CardPaymentFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(JUDO_PAYMENT, payment);
        cardPaymentFragment.setArguments(arguments);
        cardPaymentFragment.setListener(paymentListener);

        return cardPaymentFragment;
    }

    public void setListener(PaymentListener listener) {
        this.listener = listener;
    }

}