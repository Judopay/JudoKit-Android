package com.judopay.view;

import android.content.Context;
import androidx.annotation.StringRes;
import com.google.android.material.textfield.TextInputLayout;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.judopay.R;
import com.judopay.model.Country;

public class PostcodeEntryView extends FrameLayout {

    private TextInputLayout postcodeInputLayout;

    public PostcodeEntryView(final Context context) {
        super(context);
        initialize();
    }

    public PostcodeEntryView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PostcodeEntryView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_postcode_entry, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        postcodeInputLayout = findViewById(R.id.post_code_input_layout);
        final JudoEditText editText = findViewById(R.id.post_code_edit_text);

        if (editText != null) {
            editText.setOnFocusChangeListener(new HintFocusListener(editText, getResources().getString(R.string.empty)));
        }
    }

    public void addTextChangedListener(final TextWatcher watcher) {
        EditText editText = postcodeInputLayout.getEditText();
        if (editText != null) {
            editText.addTextChangedListener(watcher);
        }
    }

    private void setHint(@StringRes final int hint) {
        postcodeInputLayout.setHint(getResources().getString(hint));
    }

    public void setError(@StringRes final int error, final boolean show) {
        postcodeInputLayout.setErrorEnabled(show);

        if (show) {
            postcodeInputLayout.setError(getResources().getString(error));
        } else {
            postcodeInputLayout.setError("");
        }
    }

    private void setNumericInput(final boolean numeric) {
        final EditText editText = postcodeInputLayout.getEditText();
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
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        postcodeInputLayout.setEnabled(enabled);
    }

    public String getText() {
        EditText editText = postcodeInputLayout.getEditText();
        return editText == null ? "" : editText.getText().toString().trim();
    }

    public JudoEditText getEditText() {
        return (JudoEditText) postcodeInputLayout.getEditText();
    }

    public void setCountry(final Country country) {
        setHint(country.getPostcodeNameResourceId());

        boolean postcodeNumeric = Country.UNITED_STATES.equals(country);
        setNumericInput(postcodeNumeric);

        setEnabled(!Country.OTHER.equals(country));
    }
}