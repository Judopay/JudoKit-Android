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
import com.judopay.detector.AppResumeDetector;
import com.judopay.detector.CompletedFieldsDetector;
import com.judopay.detector.PastedFieldsDetector;
import com.judopay.detector.TotalKeystrokesDetector;
import com.judopay.model.Card;
import com.judopay.validation.ValidationManager;

import java.util.ArrayList;

import static com.judopay.arch.TextUtil.isEmpty;

public abstract class AbstractCardEntryFragment extends Fragment implements ValidationManager.OnChangeListener {

    private static final String KEY_APP_RESUMED_TIMINGS = "AppResumedTimings";
    private static final String KEY_APP_PAUSE_COUNT = "AppPauseCount";

    protected static final String KEY_PASTED_FIELDS = "PastedFields";
    protected static final String KEY_KEYSTROKES = "Keystrokes";
    protected static final String KEY_COMPLETED_FIELDS = "CompletedFields";
    protected static final String KEY_FIELD_STATES = "FieldStates";

    private String buttonLabel;
    protected Button submitButton;

    CardEntryListener cardEntryListener;

    protected AppResumeDetector appResumeDetector;
    protected PastedFieldsDetector pastedFieldsDetector;
    protected TotalKeystrokesDetector keystrokesDetector;
    protected CompletedFieldsDetector completedFieldsDetector;

    public void setCardEntryListener(CardEntryListener cardEntryListener) {
        this.cardEntryListener = cardEntryListener;
    }

    protected abstract void onInitialize(Bundle savedInstanceState, Judo judo);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Judo.JUDO_OPTIONS)) {
            Judo judo = getArguments().getParcelable(Judo.JUDO_OPTIONS);

            if (judo != null) {
                setButtonLabelText(getButtonLabel());
                onInitialize(savedInstanceState, judo);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appResumeDetector = new AppResumeDetector();

        if (savedInstanceState != null) {
            //noinspection unchecked
            appResumeDetector.setResumedTimings((ArrayList<Long>) savedInstanceState.getSerializable(KEY_APP_RESUMED_TIMINGS));
            appResumeDetector.setPauseCount(savedInstanceState.getInt(KEY_APP_PAUSE_COUNT));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        appResumeDetector.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        appResumeDetector.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (keystrokesDetector != null) {
            outState.putInt(KEY_KEYSTROKES, keystrokesDetector.getTotalKeystrokes());
        }

        if (pastedFieldsDetector != null) {
            outState.putSerializable(KEY_PASTED_FIELDS, pastedFieldsDetector.getPasteTimings());
        }

        if (completedFieldsDetector != null) {
            outState.putParcelableArrayList(KEY_COMPLETED_FIELDS, completedFieldsDetector.getCompletedFields());
            outState.putSerializable(KEY_FIELD_STATES, completedFieldsDetector.getFieldStateMap());
        }

        if (appResumeDetector != null) {
            outState.putSerializable(KEY_APP_RESUMED_TIMINGS, appResumeDetector.getResumedTimings());
            outState.putInt(KEY_APP_PAUSE_COUNT, appResumeDetector.getPauseCount());
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

        return ThemeUtil.getStringAttr(getActivity(), R.attr.buttonLabel);
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