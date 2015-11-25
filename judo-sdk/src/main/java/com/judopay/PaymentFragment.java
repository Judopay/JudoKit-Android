package com.judopay;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.judopay.arch.api.RetrofitFactory;
import com.judopay.customer.Card;

import static com.judopay.JudoPay.JUDO_AMOUNT;
import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_CURRENCY;
import static com.judopay.JudoPay.JUDO_ID;
import static com.judopay.JudoPay.JUDO_META_DATA;
import static com.judopay.JudoPay.JUDO_PAYMENT_REF;

public class PaymentFragment extends BasePaymentFragment {

    private PaymentPresenter presenter;
    private Consumer consumer;
    private String judoId;
    private String amount;
    private String currency;
    private String paymentRef;
    private Bundle metaData;

    public static PaymentFragment newInstance(String judoId, String amount, String currency, String paymentRef, Consumer consumer, Bundle metaData) {
        PaymentFragment paymentFragment = new PaymentFragment();

        Bundle args = new Bundle();
        args.putString(JudoPay.JUDO_ID, judoId);
        args.putString(JudoPay.JUDO_AMOUNT, amount);
        args.putString(JudoPay.JUDO_CURRENCY, currency);
        args.putString(JudoPay.JUDO_PAYMENT_REF, paymentRef);
        args.putParcelable(JudoPay.JUDO_CONSUMER, consumer);
        args.putBundle(JudoPay.JUDO_META_DATA, metaData);

        paymentFragment.setArguments(args);

        return paymentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        consumer = args.getParcelable(JUDO_CONSUMER);
        judoId = args.getString(JUDO_ID);
        amount = args.getString(JUDO_AMOUNT);
        currency = args.getString(JUDO_CURRENCY);
        paymentRef = args.getString(JUDO_PAYMENT_REF);
        metaData = args.getBundle(JUDO_META_DATA);

        if (savedInstanceState == null) {
            this.presenter = new PaymentPresenter(this, RetrofitFactory.getApiService(), new AndroidScheduler());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        presenter.performPayment(card, consumer, judoId, amount, currency, paymentRef, metaData, JudoPay.isThreeDSecureEnabled());
    }
}