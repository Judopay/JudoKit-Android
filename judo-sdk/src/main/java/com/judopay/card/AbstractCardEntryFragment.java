package com.judopay.card;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.model.Card;
import com.judopay.validation.ValidationManager;

public abstract class AbstractCardEntryFragment extends Fragment implements ValidationManager.OnChangeListener {

    JudoOptions options;
    CardEntryListener cardEntryListener;

    public void setCardEntryListener(CardEntryListener cardEntryListener) {
        this.cardEntryListener = cardEntryListener;
    }

    protected abstract void onInitialize(JudoOptions options);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Judo.JUDO_OPTIONS)) {
            this.options = getArguments().getParcelable(Judo.JUDO_OPTIONS);
            if (options != null) {
                onInitialize(options);
            }
        }
    }

    void hideKeyboard() {
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public abstract void setCard(Card card);

}