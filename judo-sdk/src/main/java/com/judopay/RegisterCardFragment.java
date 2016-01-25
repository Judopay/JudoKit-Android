package com.judopay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.judopay.api.JudoApiServiceFactory;
import com.judopay.card.CardEntryFragment;
import com.judopay.card.CardEntryListener;
import com.judopay.model.Card;

public class RegisterCardFragment extends BaseFragment implements PaymentFormView, CardEntryListener {

    private RegisterCardPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.presenter == null) {
            this.presenter = new RegisterCardPresenter(this, JudoApiServiceFactory.getInstance(getActivity()), new AndroidScheduler(), new Gson());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_card, container, false);
    }

    @Override
    protected JudoOptions getJudoOptions() {
        Bundle args = getArguments();

        if (args.containsKey(Judo.JUDO_OPTIONS)) {
            JudoOptions judoOptions = args.getParcelable(Judo.JUDO_OPTIONS);

            return new JudoOptions.Builder()
                    .setJudoId(judoOptions.getJudoId())
                    .setConsumerRef(judoOptions.getConsumerRef())
                    .setCardNumber(judoOptions.getCardNumber())
                    .setExpiryMonth(judoOptions.getExpiryMonth())
                    .setExpiryYear(judoOptions.getExpiryYear())
                    .setSecureServerMessageShown(judoOptions.isSecureServerMessageShown())
                    .setButtonLabel(getString(R.string.add_card))
                    .build();
        } else {
            return new JudoOptions.Builder()
                    .setJudoId(args.getString(Judo.JUDO_ID))
                    .setButtonLabel(getString(R.string.add_card))
                    .setConsumerRef(args.getString(Judo.JUDO_CONSUMER))
                    .build();
        }
    }

    @Override
    protected CardEntryFragment createPaymentFormFragment() {
        return CardEntryFragment.newInstance(getJudoOptions(), this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        JudoOptions options = getJudoOptions();

        presenter.performRegisterCard(card, options);
    }

    public boolean isPaymentInProgress() {
        return this.presenter.loading;
    }
}