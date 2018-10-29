package com.judopay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.judopay.arch.Logger;
import com.judopay.model.Card;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public final class PreAuthFragment extends JudoFragment {

    private PreAuthPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Judo judo = getJudo();
        checkJudoOptionsExtras(judo.getAmount(), judo.getCurrency());

        if (this.presenter == null) {
            JudoApiService apiService = judo.getApiService(getActivity(), Judo.UI_CLIENT_MODE_JUDO_SDK);
            this.presenter = new PreAuthPresenter(this, apiService, new Logger());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.reconnect();
    }

    @Override
    public void onSubmit(Card card) {
        Judo judo = getJudo();

        if (judo.getCardToken() != null) {
            disposables.add(presenter.performTokenPreAuth(card, judo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(presenter.callback(), presenter.error()));
        } else {
            disposables.add(presenter.performPreAuth(card, judo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(presenter.callback(), presenter.error()));
        }
    }

    @Override
    boolean isTransactionInProgress() {
        return this.presenter.loading;
    }
}
