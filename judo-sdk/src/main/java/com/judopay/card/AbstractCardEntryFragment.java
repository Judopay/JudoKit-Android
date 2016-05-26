package com.judopay.card;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.judopay.Judo;
import com.judopay.R;
import com.judopay.arch.ThemeUtil;
import com.judopay.model.Card;
import com.judopay.validation.ValidationManager;

import static com.judopay.arch.TextUtil.isEmpty;

public abstract class AbstractCardEntryFragment extends Fragment implements ValidationManager.OnChangeListener {

    private String buttonLabel;

    Judo judo;
    CardEntryListener cardEntryListener;

    public void setCardEntryListener(CardEntryListener cardEntryListener) {
        this.cardEntryListener = cardEntryListener;
    }

    protected abstract void onInitialize(Judo options);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Judo.JUDO_OPTIONS)) {
            this.judo = getArguments().getParcelable(Judo.JUDO_OPTIONS);
            if (judo != null) {
                onInitialize(judo);
            }
        }
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    protected String getButtonLabel() {
        String label = ThemeUtil.getStringAttr(getActivity(), getClass(), R.attr.buttonLabel);

        if (!isEmpty(label)) {
            return label;
        }
        return buttonLabel;
    }

    void hideKeyboard() {
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setCard(Card card) { }

}