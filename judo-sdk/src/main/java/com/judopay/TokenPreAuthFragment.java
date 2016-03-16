package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.judopay.arch.AndroidScheduler;
import com.judopay.model.Card;

public class TokenPreAuthFragment extends AbstractTokenFragment {

    private TokenPreAuthPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.presenter == null) {
            JudoApiService apiService = Judo.getApiService(getActivity(), Judo.UI_CLIENT_MODE_JUDO_SDK);
            this.presenter = new TokenPreAuthPresenter(this, apiService, new AndroidScheduler(), new Gson());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        JudoOptions judoOptions = getJudoOptions();

        presenter.performTokenPreAuth(card, judoOptions);
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }

}