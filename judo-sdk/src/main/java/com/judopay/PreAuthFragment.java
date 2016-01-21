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
        JudoOptions options = getJudoOptions();

        presenter.performPreAuth(card, options);
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }
}