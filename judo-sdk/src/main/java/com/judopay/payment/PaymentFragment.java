package com.judopay.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.judopay.Client;
import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.customer.Location;
import com.judopay.payment.form.PaymentFormFragment;

import rx.Observer;

import static com.judopay.JudoPay.EXTRA_PAYMENT;

public class PaymentFragment extends Fragment implements PaymentFormListener {

    private PaymentService paymentService;
    private PaymentListener paymentListener;
    private ProgressBar progressBar;

    public static PaymentFragment newInstance(Payment payment) {
        PaymentFragment paymentFragment = new PaymentFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(JudoPay.EXTRA_PAYMENT, payment);
        paymentFragment.setArguments(arguments);

        return paymentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        setRetainInstance(true);
        paymentService = new PaymentService();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        Payment payment = getArguments().getParcelable(JudoPay.EXTRA_PAYMENT);
        PaymentFormFragment paymentFormFragment = PaymentFormFragment.newInstance(payment, this);

        if(savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, paymentFormFragment)
                    .commit();
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

    public void setPaymentListener(PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }

    private void performPayment(Transaction transaction) {
        paymentService.payment(transaction)
                .subscribe(new Observer<PaymentResponse>() {
                    @Override
                    public void onCompleted() {
                        hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(PaymentResponse paymentResponse) {
                        if(paymentResponse.isSuccess()) {
                            paymentListener.onPaymentSuccess(paymentResponse);
                        } else {
                            paymentListener.onPaymentDeclined(paymentResponse);
                        }
                    }
                });
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

}