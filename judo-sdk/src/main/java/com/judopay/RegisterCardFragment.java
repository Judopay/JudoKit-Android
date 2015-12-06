package com.judopay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.arch.api.ApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Consumer;
import com.judopay.payment.form.PaymentFormFragment;
import com.judopay.payment.form.PaymentFormListener;
import com.judopay.payment.form.PaymentFormOptions;

public class RegisterCardFragment extends BasePaymentFragment implements PaymentFormView, PaymentFormListener {

    private RegisterCardPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new RegisterCardPresenter(this, ApiServiceFactory.getApiService(getActivity()), new AndroidScheduler());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_card, container, false);
    }

    @Override
    protected PaymentFormFragment createPaymentFormFragment() {
        CardToken cardToken = getArguments().getParcelable(JudoPay.JUDO_CARD_TOKEN);
        String buttonLabel = getString(R.string.add_card);

        PaymentFormOptions paymentFormOptions = new PaymentFormOptions.Builder()
                .setCardToken(cardToken)
                .setButtonLabel(buttonLabel)
                .build();

        return PaymentFormFragment.newInstance(paymentFormOptions, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        Consumer consumer = getArguments().getParcelable(JudoPay.JUDO_CONSUMER);
        String judoId = getArguments().getString(JudoPay.JUDO_ID);

        presenter.performRegisterCard(judoId, card, consumer, JudoPay.isThreeDSecureEnabled());
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }
}