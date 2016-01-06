package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.judopay.api.JudoApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.CardToken;

public final class TokenPaymentFragment extends BaseFragment {

    private TokenPaymentPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new TokenPaymentPresenter(this, JudoApiServiceFactory.getInstance(getActivity()), new AndroidScheduler(), new Gson());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        Bundle args = getArguments();

        String consumerRef = args.getString(JudoPay.JUDO_CONSUMER);
        CardToken cardToken = args.getParcelable(JudoPay.JUDO_CARD_TOKEN);
        String judoId = args.getString(JudoPay.JUDO_ID);
        String amount = args.getString(JudoPay.JUDO_AMOUNT);
        String currency = args.getString(JudoPay.JUDO_CURRENCY);
        Bundle metaData = args.getBundle(JudoPay.JUDO_META_DATA);

        presenter.performTokenPayment(card, cardToken, consumerRef, judoId, amount, currency, metaData, JudoPay.isThreeDSecureEnabled());
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }

}