package com.judopay.card;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.judopay.Judo;
import com.judopay.R;
import com.judopay.arch.ThemeUtil;
import com.judopay.model.Card;
import com.judopay.validation.ValidationManager;

import static com.judopay.arch.TextUtil.isEmpty;

public abstract class AbstractCardEntryFragment extends Fragment implements ValidationManager.OnChangeListener {
    protected Button submitButton;
    CardEntryListener cardEntryListener;
    private String buttonLabel;

    public void setCardEntryListener(final CardEntryListener cardEntryListener) {
        this.cardEntryListener = cardEntryListener;
    }

    protected abstract void onInitialize(@Nullable final Bundle savedInstanceState, final Judo judo);

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Judo.JUDO_OPTIONS)) {
            Judo judo = getArguments().getParcelable(Judo.JUDO_OPTIONS);

            if (judo != null) {
                setButtonLabelText(getButtonLabel());
                onInitialize(savedInstanceState, judo);
            }
        }
    }

    private void setButtonLabelText(final String buttonLabel) {
        if (this.submitButton != null && !isEmpty(buttonLabel)) {
            this.submitButton.setText(buttonLabel);
        }
    }

    protected String getButtonLabel() {
        if (!isEmpty(buttonLabel)) {
            return buttonLabel;
        }

        if (getActivity() != null) {
            return ThemeUtil.getStringAttr(getActivity(), R.attr.buttonLabel);
        }
        return "";
    }

    public void setButtonLabel(final String buttonLabel) {
        this.buttonLabel = buttonLabel;
        setButtonLabelText(buttonLabel);
    }

    void hideKeyboard() {
        if (getActivity() != null) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    public void setCard(final Card card) {
    }
}
