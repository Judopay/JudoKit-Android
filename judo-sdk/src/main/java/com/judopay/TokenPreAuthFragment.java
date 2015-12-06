package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.judopay.arch.api.ApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Consumer;

public class TokenPreAuthFragment extends BasePaymentFragment {

    private TokenPreAuthPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new TokenPreAuthPresenter(this, ApiServiceFactory.getApiService(getActivity()), new AndroidScheduler());
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

        String consumerRef = args.getParcelable(JudoPay.JUDO_CONSUMER);
        CardToken cardToken = args.getParcelable(JudoPay.JUDO_CARD_TOKEN);
        String judoId = args.getString(JudoPay.JUDO_ID);
        String amount = args.getString(JudoPay.JUDO_AMOUNT);
        String currency = args.getString(JudoPay.JUDO_CURRENCY);
        Bundle metaData = args.getBundle(JudoPay.JUDO_META_DATA);

        presenter.performTokenPreAuth(card, cardToken, consumerRef, judoId, amount, currency, metaData, JudoPay.isThreeDSecureEnabled());
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }

}