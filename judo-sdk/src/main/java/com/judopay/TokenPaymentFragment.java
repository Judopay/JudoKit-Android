package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.judopay.arch.api.ApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Consumer;

import static com.judopay.JudoPay.JUDO_AMOUNT;
import static com.judopay.JudoPay.JUDO_CARD_TOKEN;
import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_CURRENCY;
import static com.judopay.JudoPay.JUDO_ID;
import static com.judopay.JudoPay.JUDO_META_DATA;
import static com.judopay.JudoPay.JUDO_PAYMENT_REF;

public class TokenPaymentFragment extends BasePaymentFragment {

    private TokenPaymentPresenter presenter;
    private CardToken cardToken;
    private Bundle metaData;
    private String paymentRef;
    private String currency;
    private String amount;
    private String judoId;
    private Consumer consumer;

    public static TokenPaymentFragment newInstance(String judoId, String amount, String currency, CardToken cardToken, String paymentRef, Consumer consumer, Bundle metaData) {
        TokenPaymentFragment tokenPaymentFragment = new TokenPaymentFragment();

        Bundle args = new Bundle();
        args.putString(JudoPay.JUDO_ID, judoId);
        args.putString(JudoPay.JUDO_AMOUNT, amount);
        args.putString(JudoPay.JUDO_CURRENCY, currency);
        args.putString(JudoPay.JUDO_PAYMENT_REF, paymentRef);
        args.putParcelable(JudoPay.JUDO_CONSUMER, consumer);
        args.putBundle(JudoPay.JUDO_META_DATA, metaData);
        args.putParcelable(JudoPay.JUDO_CARD_TOKEN, cardToken);

        tokenPaymentFragment.setArguments(args);

        return tokenPaymentFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
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
        cardToken = args.getParcelable(JUDO_CARD_TOKEN);
        consumer = args.getParcelable(JUDO_CONSUMER);

        if (savedInstanceState == null) {
            this.presenter = new TokenPaymentPresenter(this, ApiServiceFactory.getApiService(getActivity()), new AndroidScheduler());
        }
    }

    @Override
    public void onSubmit(Card card) {
        presenter.performTokenPayment(card, cardToken, consumer, judoId, amount, currency, paymentRef, metaData, JudoPay.isThreeDSecureEnabled());
    }
}