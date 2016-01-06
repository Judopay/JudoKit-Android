package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.judopay.api.JudoApiServiceFactory;
import com.judopay.model.Card;

public final class PreAuthFragment extends BaseFragment {

    private PreAuthPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new PreAuthPresenter(this, JudoApiServiceFactory.getInstance(getActivity()), new AndroidScheduler(), new Gson());
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
        String judoId = args.getString(JudoPay.JUDO_ID);
        String amount = args.getString(JudoPay.JUDO_AMOUNT);
        String currency = args.getString(JudoPay.JUDO_CURRENCY);
        Bundle metaData = args.getBundle(JudoPay.JUDO_META_DATA);

        presenter.performPreAuth(card, consumerRef, judoId, amount, currency, metaData, JudoPay.isThreeDSecureEnabled());
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }
}