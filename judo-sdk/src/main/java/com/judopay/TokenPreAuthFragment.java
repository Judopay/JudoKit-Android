package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.judopay.api.JudoApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.CardToken;

public class TokenPreAuthFragment extends BaseFragment {

    private TokenPreAuthPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new TokenPreAuthPresenter(this, JudoApiServiceFactory.getInstance(getActivity()), new AndroidScheduler(), new Gson());
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

        String consumerRef = args.getString(Judo.JUDO_CONSUMER);
        CardToken cardToken = args.getParcelable(Judo.JUDO_CARD_TOKEN);
        String judoId = args.getString(Judo.JUDO_ID);
        String amount = args.getString(Judo.JUDO_AMOUNT);
        String currency = args.getString(Judo.JUDO_CURRENCY);
        Bundle metaData = args.getBundle(Judo.JUDO_META_DATA);

        presenter.performTokenPreAuth(card, cardToken, consumerRef, judoId, amount, currency, metaData);
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }

}