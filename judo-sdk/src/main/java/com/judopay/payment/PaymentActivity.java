package com.judopay.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.judopay.Client;
import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.customer.Location;
import com.judopay.payment.form.PaymentFormFragment;

import static com.judopay.JudoPay.EXTRA_PAYMENT;

public class PaymentActivity extends AppCompatActivity implements PaymentFormListener, PaymentView {

    private static final String KEY_PAYMENT_FRAGMENT = "PaymentFormFragment";

    private ProgressBar progressBar;
    private PaymentFormFragment fragment;
    private static PaymentPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        validateParcelableExtra(EXTRA_PAYMENT);

        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        if (savedInstanceState == null) {
            presenter = new PaymentPresenter();

            Parcelable payment = getIntent().getParcelableExtra(EXTRA_PAYMENT);

            String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
            this.setTitle(title != null ? title : "Payment");

            this.fragment = PaymentFormFragment.newInstance(payment, this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        } else {
            this.fragment = (PaymentFormFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, KEY_PAYMENT_FRAGMENT);
            this.fragment.setPaymentFormListener(this);
        }

        presenter.bindView(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, KEY_PAYMENT_FRAGMENT, fragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unbindView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(JudoPay.RESULT_CANCELED);
    }

    @Override
    public void onSubmit(Card card) {
        Payment payment = getIntent().getParcelableExtra(EXTRA_PAYMENT);

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

        if(card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        presenter.performPayment(builder.build());
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setViewModel(PaymentResponse paymentResponse) {
        Intent intent = new Intent();
        intent.putExtra(JudoPay.JUDO_RECEIPT, paymentResponse);

        if(paymentResponse.isSuccess()) {
            setResult(JudoPay.RESULT_PAYMENT_SUCCESS, intent);
        } else {
            setResult(JudoPay.RESULT_PAYMENT_DECLINED, intent);
        }

        finish();
    }

    private void validateParcelableExtra(String extraName) {
        Parcelable extra = getIntent().getParcelableExtra(extraName);
        if (extra == null) {
            throw new IllegalArgumentException(String.format("%s extra must be supplied to %s", extraName,
                    this.getClass().getSimpleName()));
        }
    }

}