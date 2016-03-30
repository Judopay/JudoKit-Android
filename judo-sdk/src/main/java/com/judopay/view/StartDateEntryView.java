package com.judopay.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.judopay.R;

public class StartDateEntryView extends FrameLayout {

    private EditText startDateEditText;
    private TextInputLayout startDateInputLayout;

    public StartDateEntryView(Context context) {
        super(context);
        initialize();
    }

    public StartDateEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public StartDateEntryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_start_date_entry, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        startDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
        startDateInputLayout = (TextInputLayout) findViewById(R.id.start_date_input_layout);

        HintFocusListener hintFocusListener = new HintFocusListener(startDateEditText, R.string.date_hint);
        startDateEditText.setOnFocusChangeListener(hintFocusListener);

        String dateFormat = getResources().getString(R.string.date_format);
        NumberFormatTextWatcher numberFormatTextWatcher = new NumberFormatTextWatcher(startDateEditText, dateFormat, true);
        startDateEditText.addTextChangedListener(numberFormatTextWatcher);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        startDateEditText.addTextChangedListener(watcher);
    }

    public String getText() {
        return startDateEditText.getText().toString().trim();
    }

    public void setError(@StringRes int error, boolean show) {
        startDateInputLayout.setErrorEnabled(show);

        if (show) {
            startDateInputLayout.setError(getResources().getString(error));
        } else {
            startDateInputLayout.setError("");
        }
    }

}