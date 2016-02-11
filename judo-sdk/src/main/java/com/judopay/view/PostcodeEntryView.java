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

    private EditText postcodeEditText;
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

        postcodeEditText = (EditText) findViewById(R.id.post_code_edit_text);
        postcodeInputLayout = (TextInputLayout) findViewById(R.id.post_code_input_layout);

        onFocusChangeListener = new CompositeOnFocusChangeListener(
                new HintFocusListener(postcodeEditText, R.string.empty));

        postcodeEditText.setOnFocusChangeListener(onFocusChangeListener);
    }

    public void addOnFocusChangeListener(OnFocusChangeListener listener) {
        onFocusChangeListener.add(listener);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        postcodeEditText.addTextChangedListener(watcher);
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
        if (numeric && postcodeEditText.getInputType() != InputType.TYPE_CLASS_NUMBER) {
            postcodeEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            int alphanumericInputTypes = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;

            if (!numeric && postcodeEditText.getInputType() != alphanumericInputTypes) {
                postcodeEditText.setInputType(alphanumericInputTypes);
            }
        }

        postcodeEditText.setPrivateImeOptions("nm"); // prevent text suggestions in keyboard
    }

    public void setSelectionEnd() {
        postcodeEditText.setSelection(postcodeEditText.getText().length());
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        postcodeEditText.setEnabled(enabled);
    }

    public String getText() {
        return postcodeEditText.getText().toString().trim();
    }
}