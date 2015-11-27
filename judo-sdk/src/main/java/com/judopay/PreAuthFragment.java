package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.judopay.arch.api.ApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.Consumer;

import static com.judopay.JudoPay.JUDO_AMOUNT;
import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_CURRENCY;
import static com.judopay.JudoPay.JUDO_ID;
import static com.judopay.JudoPay.JUDO_META_DATA;
import static com.judopay.JudoPay.JUDO_PAYMENT_REF;

public class PreAuthFragment extends BasePaymentFragment {

    private PreAuthPresenter presenter;
    private Consumer consumer;
    private Bundle metaData;
    private String paymentRef;
    private String currency;
    private String amount;
    private String judoId;

    public static PreAuthFragment newInstance(String judoId, String amount, String currency, String paymentRef, Consumer consumer, Bundle metaData) {
        PreAuthFragment preAuthFragment = new PreAuthFragment();

        Bundle args = new Bundle();
        args.putString(JudoPay.JUDO_ID, judoId);
        args.putString(JudoPay.JUDO_AMOUNT, amount);
        args.putString(JudoPay.JUDO_CURRENCY, currency);
        args.putString(JudoPay.JUDO_PAYMENT_REF, paymentRef);
        args.putParcelable(JudoPay.JUDO_CONSUMER, consumer);
        args.putBundle(JudoPay.JUDO_META_DATA, metaData);

        preAuthFragment.setArguments(args);
        return preAuthFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        judoId = args.getString(JUDO_ID);
        amount = args.getString(JUDO_AMOUNT);
        currency = args.getString(JUDO_CURRENCY);
        paymentRef = args.getString(JUDO_PAYMENT_REF);
        metaData = args.getBundle(JUDO_META_DATA);
        consumer = args.getParcelable(JUDO_CONSUMER);

        if (savedInstanceState == null) {
            this.presenter = new PreAuthPresenter(this, ApiServiceFactory.getApiService(), new AndroidScheduler());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        presenter.performPreAuth(card, consumer, judoId, amount, currency, paymentRef, metaData, JudoPay.isThreeDSecureEnabled());
    }
}