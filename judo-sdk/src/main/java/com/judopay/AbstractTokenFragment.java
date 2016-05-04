package com.judopay;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

abstract class AbstractTokenFragment extends JudoFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        JudoOptions judoOptions = getJudoOptions();

        if (judoOptions.getCardToken().isExpired()) {
            PendingIntent pendingResult = getActivity().createPendingResult(Judo.JUDO_REQUEST, new Intent(), 0);
            try {
                pendingResult.send(Judo.RESULT_TOKEN_EXPIRED);
            } catch (PendingIntent.CanceledException ignore) { }
        }
    }

    @Override
    boolean isTransactionInProgress() {
        return false;
    }
}