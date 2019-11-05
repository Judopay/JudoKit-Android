package com.judopay;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.arch.Logger;
import com.judopay.card.AbstractCardEntryFragment;
import com.judopay.card.CardEntryFragment;
import com.judopay.card.CardEntryListener;
import com.judopay.model.Card;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CheckCardFragment extends JudoFragment implements TransactionCallbacks, CardEntryListener {
    private CheckCardPresenter presenter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.presenter == null) {
            JudoApiService apiService = getJudo().getApiService(getActivity(), Judo.UI_CLIENT_MODE_JUDO_SDK);
            this.presenter = new CheckCardPresenter(this, apiService, new Logger());
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLoadingText(R.string.checking_card);
        presenter.reconnect();
    }

    @Override
    AbstractCardEntryFragment createCardEntryFragment() {
        CardEntryFragment cardEntryFragment = CardEntryFragment.newInstance(getJudo(), this);
        cardEntryFragment.setButtonLabel(getString(R.string.check_card));
        return cardEntryFragment;
    }

    @Override
    public void onSubmit(final Card card) {
        disposables.add(presenter.performCheckCard(card, getJudo())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(presenter.callback(), presenter.error()));
    }

    @Override
    boolean isTransactionInProgress() {
        return this.presenter.loading;
    }
}
