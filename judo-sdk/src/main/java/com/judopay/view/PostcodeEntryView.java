package com.judopay.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.judopay.R;

public class PostcodeEntryView extends FrameLayout {

    private TextInputLayout postcodeInputLayout;
    private CompositeOnFocusChangeListener onFocusChangeListener;

    public PostcodeEntryView(Context context) {
        super(context);
        initialize();
    }

    public PostcodeEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PostcodeEntryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_postcode_entry, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        postcodeInputLayout = (TextInputLayout) findViewById(R.id.post_code_input_layout);
        EditText editText = postcodeInputLayout.getEditText();

        if (editText != null) {
            onFocusChangeListener = new CompositeOnFocusChangeListener(
                    new HintFocusListener(editText, R.string.empty));

            editText.setOnFocusChangeListener(onFocusChangeListener);
        }
    }

    public void addOnFocusChangeListener(OnFocusChangeListener listener) {
        onFocusChangeListener.add(listener);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        EditText editText = postcodeInputLayout.getEditText();
        if (editText != null) {
            editText.addTextChangedListener(watcher);
        }
    }

    public void setHint(@StringRes int hint) {
        postcodeInputLayout.setHint(getResources().getString(hint));
    }

    public void setError(@StringRes int error, boolean show) {
        postcodeInputLayout.setErrorEnabled(show);

        if (show) {
            postcodeInputLayout.setError(getResources().getString(error));
        } else {
            postcodeInputLayout.setError("");
        }
    }

    public void setNumericInput(boolean numeric) {
        EditText editText = postcodeInputLayout.getEditText();
        if (editText != null) {
            if (numeric && editText.getInputType() != InputType.TYPE_CLASS_NUMBER) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                int alphanumericInputTypes = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;

                if (!numeric && editText.getInputType() != alphanumericInputTypes) {
                    editText.setInputType(alphanumericInputTypes);
                }
            }

            editText.setPrivateImeOptions("nm"); // prevent text suggestions in keyboard
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        postcodeInputLayout.setEnabled(enabled);
    }

    public String getText() {
        EditText editText = postcodeInputLayout.getEditText();
        return editText == null ? "" : editText.getText().toString().trim();
    }
}