package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.judopay.arch.AndroidScheduler;
import com.judopay.model.Card;

import static com.judopay.Judo.JUDO_OPTIONS;

public final class PaymentFragment extends BaseFragment {

    private PaymentPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JudoOptions options = getArguments().getParcelable(JUDO_OPTIONS);
        checkJudoOptionsExtras(options.getAmount(), options.getJudoId(), options.getCurrency(), options.getConsumerRef());

        if (this.presenter == null) {
            this.presenter = new PaymentPresenter(this, Judo.getApiService(getActivity()), new AndroidScheduler(), new Gson());
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