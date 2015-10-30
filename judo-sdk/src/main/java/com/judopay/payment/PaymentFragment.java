package com.judopay.payment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.Client;
import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.arch.api.RetrofitFactory;
import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.customer.Location;
import com.judopay.payment.form.PaymentFormFragment;

import rx.Observer;

import static com.judopay.JudoPay.EXTRA_PAYMENT;

public class PaymentFragment extends Fragment implements PaymentFormListener {

    public static final String TAG_PAYMENT_FORM = "PaymentFormFragment";

    private View progressBar;

    private PaymentListener paymentListener;
    private PaymentApiService paymentApiService;

    private boolean paymentInProgress;

    public static PaymentFragment newInstance(Payment payment, PaymentListener listener) {
        PaymentFragment paymentFragment = new PaymentFragment();
        paymentFragment.paymentListener = listener;

        Bundle arguments = new Bundle();
        arguments.putParcelable(JudoPay.EXTRA_PAYMENT, payment);
        paymentFragment.setArguments(arguments);

        return paymentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        this.paymentApiService = RetrofitFactory.getInstance()
                .create(PaymentApiService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressBar = view.findViewById(R.id.progress_container);

        if(paymentInProgress) {
            showLoading();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getFragmentManager();

        PaymentFormFragment paymentFormFragment = (PaymentFormFragment) fm.findFragmentByTag(TAG_PAYMENT_FORM);

        if (paymentFormFragment == null) {
            paymentFormFragment = PaymentFormFragment.newInstance(getArguments().getParcelable(EXTRA_PAYMENT), this);
            paymentFormFragment.setTargetFragment(this, 0);

            fm.beginTransaction()
                    .add(R.id.container, paymentFormFragment, TAG_PAYMENT_FORM)
                    .commit();
        } else {
            paymentFormFragment.setPaymentFormListener(this);
        }
    }

    @Override
    public void onSubmit(Card card) {
        Payment payment = getArguments().getParcelable(EXTRA_PAYMENT);

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

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        performPayment(builder.build());
    }

    private void performPayment(Transaction transaction) {
        onLoadStarted();
        paymentApiService.payment(transaction)
                .subscribe(new Observer<PaymentResponse>() {
                    @Override
                    public void onCompleted() {
                        onLoadFinished();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLoadFinished();
                    }

                    @Override
                    public void onNext(PaymentResponse paymentResponse) {
                        if (paymentResponse.isSuccess()) {
                            paymentListener.onPaymentSuccess(paymentResponse);
                        } else {
                            paymentListener.onPaymentDeclined(paymentResponse);
                        }
                    }
                });
    }

    private void onLoadFinished() {
        progressBar.setVisibility(View.GONE);
        paymentInProgress = false;
    }

    private void onLoadStarted() {
        showLoading();
        paymentInProgress = true;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

}
