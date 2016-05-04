package com.judopay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.judopay.arch.AndroidScheduler;
import com.judopay.card.AbstractCardEntryFragment;
import com.judopay.card.CardEntryFragment;
import com.judopay.card.CardEntryListener;
import com.judopay.model.Card;

import static com.judopay.Judo.JUDO_OPTIONS;

public class RegisterCardFragment extends BaseFragment implements TransactionCallbacks, CardEntryListener {

    private RegisterCardPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JudoOptions options = getArguments().getParcelable(JUDO_OPTIONS);
        checkJudoOptionsExtras(options.getConsumerRef(), options.getJudoId());

        if (this.presenter == null) {
            JudoApiService apiService = Judo.getApiService(getActivity(), Judo.UI_CLIENT_MODE_JUDO_SDK);
            this.presenter = new RegisterCardPresenter(this, apiService, new AndroidScheduler(), new Gson());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_card, container, false);
    }

    @Override
    protected JudoOptions getJudoOptions() {
        Bundle args = getArguments();

        JudoOptions judoOptions = args.getParcelable(Judo.JUDO_OPTIONS);

        return new JudoOptions.Builder()
                .setJudoId(judoOptions.getJudoId())
                .setConsumerRef(judoOptions.getConsumerRef())
                .setCardNumber(judoOptions.getCardNumber())
                .setExpiryMonth(judoOptions.getExpiryMonth())
                .setExpiryYear(judoOptions.getExpiryYear())
                .setSecureServerMessageShown(judoOptions.isSecureServerMessageShown())
                .setCustomLayout(judoOptions.getCustomLayout())
                .setButtonLabel(getString(R.string.add_card))
                .build();
    }

    @Override
    AbstractCardEntryFragment createCardEntryFragment() {
        return CardEntryFragment.newInstance(getJudoOptions(), this);
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