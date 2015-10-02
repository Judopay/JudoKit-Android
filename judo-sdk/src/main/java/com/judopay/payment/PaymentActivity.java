package com.judopay.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.judopay.Callback;
import com.judopay.Client;
import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.customer.Location;

import static com.judopay.JudoPay.JUDO_PAYMENT;
import static com.judopay.JudoPay.JUDO_RECEIPT;
import static com.judopay.JudoPay.RESULT_CANCELED;
import static com.judopay.JudoPay.RESULT_ERROR;
import static com.judopay.JudoPay.RESULT_PAYMENT_SUCCESS;

public class PaymentActivity extends AppCompatActivity implements PaymentFormListener {

    private PaymentService paymentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validateParcelableExtra(JUDO_PAYMENT);
        this.paymentService = new PaymentService();

        if (savedInstanceState == null) {
            Parcelable payment = getIntent().getParcelableExtra(JUDO_PAYMENT);
            Fragment fragment = CardFormFragment.newInstance(payment, this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }

    private void validateParcelableExtra(String extraName) {
        Parcelable extra = getIntent().getParcelableExtra(extraName);
        if (extra == null) {
            throw new IllegalArgumentException(String.format("%s extra must be supplied to %s", extraName,
                    this.getClass().getSimpleName()));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    public void onSubmitCard(Card card) {
        Payment payment = getIntent().getParcelableExtra(JUDO_PAYMENT);

        Transaction.Builder builder = new Transaction.Builder()
                .setAmount(String.valueOf(payment.getAmount()))
                .setCardAddress(new Address.Builder()
                        .setPostCode(card.getCardAddress().getPostcode())
                        .build())
                .setClientDetails(new Client())
                .setConsumerLocation(new Location())
                .setCardNumber(card.getCardNumber())
                .setCurrency(payment.getCurrency())
                .setCv2(card.getCv2())
                .setJudoId(payment.getJudoId())
                .setYourConsumerReference(payment.getConsumer().getYourConsumerReference())
                .setYourPaymentReference(payment.getPaymentRef())
                .setExpiryDate(card.getExpiryDate());

        paymentService.payment(builder.build(), new Callback<PaymentResponse>() {
            @Override
            public void onSuccess(PaymentResponse paymentResponse) {
                Intent intent = new Intent();
                intent.putExtra(JUDO_RECEIPT, paymentResponse);
                setResult(RESULT_PAYMENT_SUCCESS, intent);
                finish();
            }

            @Override
            public void onFailure() {
                setResult(RESULT_ERROR, new Intent());
                finish();
            }
        });
    }

}