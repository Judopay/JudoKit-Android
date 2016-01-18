package com.judopay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.judopay.api.JudoApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.payment.form.CardEntryFragment;
import com.judopay.payment.form.PaymentFormListener;
import com.judopay.payment.form.JudoOptions;

public class RegisterCardFragment extends BaseFragment implements PaymentFormView, PaymentFormListener {

    private RegisterCardPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new RegisterCardPresenter(this, JudoApiServiceFactory.getInstance(getActivity()), new AndroidScheduler(), new Gson());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_card, container, false);
    }

    @Override
    protected CardEntryFragment createPaymentFormFragment() {
        CardToken cardToken = getArguments().getParcelable(Judo.JUDO_CARD_TOKEN);
        String buttonLabel = getString(R.string.add_card);

        JudoOptions judoOptions = new JudoOptions.Builder()
                .setCardToken(cardToken)
                .setButtonLabel(buttonLabel)
                .build();

        return CardEntryFragment.newInstance(judoOptions, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        String consumerRef = getArguments().getString(Judo.JUDO_CONSUMER);
        String judoId = getArguments().getString(Judo.JUDO_ID);

        presenter.performRegisterCard(judoId, card, consumerRef);
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }
}