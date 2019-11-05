package com.judopay;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;

import static com.judopay.Judo.JUDO_OPTIONS;

public abstract class BaseFragment extends Fragment {

    protected CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Judo judo = getJudo();
        if (judo == null) {
            throw new IllegalArgumentException(String.format("%s argument is required for %s", JUDO_OPTIONS, this.getClass().getSimpleName()));
        }

        checkJudoOptionsExtras(judo.getJudoId(), judo.getConsumerReference());

        // Check if token has expired
        if (judo.getCardToken() != null && judo.getCardToken().isExpired() && getActivity() != null) {
            PendingIntent pendingResult = getActivity().createPendingResult(Judo.JUDO_REQUEST, new Intent(), 0);
            try {
                pendingResult.send(Judo.RESULT_TOKEN_EXPIRED);
            } catch (PendingIntent.CanceledException ignore) {
            }
        }

        setRetainInstance(true);
    }

    void checkJudoOptionsExtras(final Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                throw new IllegalArgumentException("Judo must contain all required fields");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    protected Judo getJudo() {
        Bundle args = getArguments();
        return args != null ? args.getParcelable(JUDO_OPTIONS) : null;
    }
}
