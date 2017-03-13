package com.judopay;

import android.os.Bundle;
import android.view.View;

import com.judopay.arch.Logger;
import com.judopay.devicedna.Credentials;
import com.judopay.model.Card;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.judopay.Judo.JUDO_OPTIONS;

public final class PreAuthFragment extends JudoFragment {

    private PreAuthPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Judo judo = getArguments().getParcelable(JUDO_OPTIONS);
        checkJudoOptionsExtras(judo.getAmount(), judo.getJudoId(), judo.getCurrency(), judo.getConsumerReference());

        if (this.presenter == null) {
            JudoApiService apiService = judo.getApiService(getActivity(), Judo.UI_CLIENT_MODE_JUDO_SDK);
            Credentials credentials = new Credentials(judo.getApiToken(), judo.getApiSecret());
            this.presenter = new PreAuthPresenter(this, apiService, new DeviceDna(getActivity(), credentials), new Logger());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card, Map<String, Object> deviceIdentifiers) {
        Judo judo = getJudo();

        if (judo.getCardToken() != null) {
            presenter.performTokenPreAuth(card, judo, deviceIdentifiers)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(presenter.callback(), presenter.error());
        } else {
            presenter.performPreAuth(card, judo, deviceIdentifiers)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(presenter.callback(), presenter.error());
        }

    }

    @Override
    boolean isTransactionInProgress() {
        return this.presenter.loading;
    }

}