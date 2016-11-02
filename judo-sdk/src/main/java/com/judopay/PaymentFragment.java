package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.judopay.arch.AndroidScheduler;
import com.judopay.model.Card;

import static com.judopay.Judo.JUDO_OPTIONS;

public final class PaymentFragment extends JudoFragment {

    private PaymentPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Judo judo = getArguments().getParcelable(JUDO_OPTIONS);
        checkJudoOptionsExtras(judo.getAmount(), judo.getJudoId(), judo.getCurrency(), judo.getReference());

        if (this.presenter == null) {
            this.presenter = new PaymentPresenter(this, judo.getApiService(getActivity(), Judo.UI_CLIENT_MODE_JUDO_SDK), new AndroidScheduler(), new Gson());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        Judo options = getJudoOptions();

        if (options.getCardToken() != null) {
            presenter.performTokenPayment(card, options);
        } else {
            presenter.performPayment(card, options);
        }
    }

    @Override
    boolean isTransactionInProgress() {
        return this.presenter.loading;
    }

}