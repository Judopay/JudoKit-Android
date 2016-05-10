package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.judopay.arch.AndroidScheduler;
import com.judopay.model.Card;

import static com.judopay.Judo.JUDO_OPTIONS;

public final class PreAuthFragment extends BaseFragment {

    private PreAuthPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JudoOptions options = getArguments().getParcelable(JUDO_OPTIONS);
        checkJudoOptionsExtras(options.getAmount(), options.getJudoId(), options.getCurrency(), options.getConsumerRef());

        if (this.presenter == null) {
            JudoApiService apiService = Judo.getApiService(getActivity(), Judo.UI_CLIENT_MODE_JUDO_SDK);
            this.presenter = new PreAuthPresenter(this, apiService, new AndroidScheduler(), new Gson());
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

        if(options.getCardToken() != null) {
            presenter.performTokenPreAuth(card, options);
        } else {
            presenter.performPreAuth(card, options);
        }
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }
}