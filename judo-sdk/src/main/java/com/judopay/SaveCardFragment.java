package com.judopay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.arch.Logger;
import com.judopay.card.AbstractCardEntryFragment;
import com.judopay.card.CardEntryFragment;
import com.judopay.card.CardEntryListener;
import com.judopay.devicedna.Credentials;
import com.judopay.model.Card;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.judopay.Judo.JUDO_OPTIONS;

public class SaveCardFragment extends JudoFragment implements TransactionCallbacks, CardEntryListener {

    private SaveCardPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Judo judo = getArguments().getParcelable(JUDO_OPTIONS);
        if (judo == null) {
            throw new IllegalArgumentException("Judo argument missing");
        }

        checkJudoOptionsExtras(judo.getConsumerReference(), judo.getJudoId());

        if (this.presenter == null) {
            JudoApiService apiService = judo.getApiService(getActivity(), Judo.UI_CLIENT_MODE_JUDO_SDK);
            Credentials credentials = new Credentials(judo.getApiToken(), judo.getApiSecret());

            DeviceDna deviceDna = new DeviceDna(getActivity(), credentials);
            this.presenter = new SaveCardPresenter(this, apiService, deviceDna, new Logger());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_card, container, false);
    }

    @Override
    AbstractCardEntryFragment createCardEntryFragment() {
        CardEntryFragment cardEntryFragment = CardEntryFragment.newInstance(getJudo(), this);
        cardEntryFragment.setButtonLabel(getString(R.string.add_card));
        return cardEntryFragment;
    }

    @Override
    public void onSubmit(Card card, Map<String, Object> userSignals) {
        Judo options = getJudo();

        presenter.performSaveCard(card, options, userSignals)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(presenter.callback(), presenter.error());
    }

    @Override
    boolean isTransactionInProgress() {
        return this.presenter.loading;
    }
}
