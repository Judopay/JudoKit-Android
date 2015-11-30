package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.judopay.arch.api.ApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.Consumer;

public final class PreAuthFragment extends BasePaymentFragment {

    private PreAuthPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new PreAuthPresenter(this, ApiServiceFactory.getApiService(getActivity()), new AndroidScheduler());
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

        Consumer consumer = args.getParcelable(JudoPay.JUDO_CONSUMER);
        String judoId = args.getString(JudoPay.JUDO_ID);
        String amount = args.getString(JudoPay.JUDO_AMOUNT);
        String currency = args.getString(JudoPay.JUDO_CURRENCY);
        String paymentRef = args.getString(JudoPay.JUDO_PAYMENT_REF);
        Bundle metaData = args.getBundle(JudoPay.JUDO_META_DATA);

        presenter.performPreAuth(card, consumer, judoId, amount, currency, paymentRef, metaData, JudoPay.isThreeDSecureEnabled());
    }
}