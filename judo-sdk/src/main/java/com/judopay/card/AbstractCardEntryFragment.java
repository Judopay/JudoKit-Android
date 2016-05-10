package com.judopay.card;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.R;
import com.judopay.arch.ThemeUtil;
import com.judopay.validation.ValidationManager;

import static com.judopay.arch.TextUtil.isEmpty;

public abstract class AbstractCardEntryFragment extends Fragment implements ValidationManager.OnChangeListener {

    private String buttonLabel;

    JudoOptions judoOptions;
    CardEntryListener cardEntryListener;

    public void setCardEntryListener(CardEntryListener cardEntryListener) {
        this.cardEntryListener = cardEntryListener;
    }

    protected abstract void onInitialize(JudoOptions options);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Judo.JUDO_OPTIONS)) {
            this.judoOptions = getArguments().getParcelable(Judo.JUDO_OPTIONS);
            if (judoOptions != null) {
                onInitialize(judoOptions);
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

}