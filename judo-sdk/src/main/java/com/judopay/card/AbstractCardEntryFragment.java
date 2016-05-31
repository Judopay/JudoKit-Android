package com.judopay.card;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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

    private String buttonLabel;
    protected Button submitButton;

    private Judo judo;
    CardEntryListener cardEntryListener;

    public void setCardEntryListener(CardEntryListener cardEntryListener) {
        this.cardEntryListener = cardEntryListener;
    }

    protected abstract void onInitialize(Judo judo);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Judo.JUDO_OPTIONS)) {
            this.judo = getArguments().getParcelable(Judo.JUDO_OPTIONS);
            if (judo != null) {
                setButtonLabelText(getButtonLabel());
                onInitialize(judo);
            }
        }
    }

    private void setButtonLabelText(String buttonLabel) {
        if (this.submitButton != null && !isEmpty(buttonLabel)) {
            this.submitButton.setText(buttonLabel);
        }
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
        setButtonLabelText(buttonLabel);
    }

    protected String getButtonLabel() {
        if (!isEmpty(buttonLabel)) {
            return buttonLabel;
        }

        return ThemeUtil.getStringAttr(getActivity(), getClass(), R.attr.buttonLabel);
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