package com.judopay.register;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.Client;
import com.judopay.Consumer;
import com.judopay.JudoApiService;
import com.judopay.R;
import com.judopay.arch.api.RetrofitFactory;
import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.payment.PaymentFormListener;
import com.judopay.payment.PaymentListener;
import com.judopay.payment.Receipt;
import com.judopay.payment.form.PaymentFormFragment;

import rx.Observer;

public class RegisterCardFragment extends Fragment implements PaymentFormListener, Observer<Receipt> {

    public static final String KEY_CONSUMER = "Judo-Consumer";

    private boolean paymentInProgress;
    private PaymentListener paymentListener;

    private JudoApiService judoApiService;

    private View progressOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        this.judoApiService = RetrofitFactory.getInstance()
                .create(JudoApiService.class);

        if (savedInstanceState == null) {
            PaymentFormFragment paymentFormFragment = PaymentFormFragment.newInstance(this, getString(R.string.add_card));
            paymentFormFragment.setRetainInstance(true);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, paymentFormFragment)
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressOverlay = view.findViewById(R.id.progress_overlay);

        if (paymentInProgress) {
            showLoading();
        }
    }

    @Override
    public void onSubmit(Card card) {
        Consumer consumer = getArguments().getParcelable(KEY_CONSUMER);

        RegisterTransaction.Builder builder = new RegisterTransaction.Builder()
                .setCardAddress(new Address.Builder()
                        .setPostCode(card.getCardAddress().getPostcode())
                        .build())
                .setClientDetails(new Client())
                .setCardNumber(card.getCardNumber())
                .setCv2(card.getCv2())
                .setExpiryDate(card.getExpiryDate());

        if (consumer != null) {
            builder.setYourConsumerReference(consumer.getYourConsumerReference());
        }

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        onLoadStarted();

        judoApiService.registerCard(builder.build())
                .subscribe(this);
    }

    public void setPaymentListener(PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }

    @Override
    public void onCompleted() {
        onLoadFinished();
    }

    @Override
    public void onError(Throwable e) {
        onLoadFinished();
    }

    @Override
    public void onNext(Receipt receipt) {
        if (receipt.isSuccess()) {
            paymentListener.onPaymentSuccess(receipt);
        } else {
            paymentListener.onPaymentDeclined(receipt);
        }
    }

    private void onLoadFinished() {
        progressOverlay.setVisibility(View.GONE);
        paymentInProgress = false;
    }

    private void onLoadStarted() {
        showLoading();
        paymentInProgress = true;
    }

    private void showLoading() {
        progressOverlay.setVisibility(View.VISIBLE);
    }

}