package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.judopay.api.JudoApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.Consumer;

public final class PaymentFragment extends BasePaymentFragment {

    private PaymentPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new PaymentPresenter(this, JudoApiServiceFactory.getInstance(getActivity()), new AndroidScheduler(), new Gson());
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

        presenter.performPayment(card, consumer, judoId, amount, currency, paymentRef, metaData, JudoPay.isThreeDSecureEnabled());
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }
}