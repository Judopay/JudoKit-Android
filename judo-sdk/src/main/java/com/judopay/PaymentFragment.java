package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.judopay.arch.AndroidScheduler;
import com.judopay.model.Card;

public final class PaymentFragment extends BaseFragment {

    private PaymentPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.presenter == null) {
            JudoApiService apiService = Judo.getApiService(getActivity());
            this.presenter = new PaymentPresenter(this, apiService, new AndroidScheduler(), new Gson());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        JudoOptions options = getJudoOptions();

        presenter.performPayment(card, options);
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }
}