package com.judopay.view;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.judopay.R;
import com.judopay.validation.Validation;

/**
 * A view that allows for a card expiry date to be entered or for a tokenized expiry number to be shown.
 */
public class ExpiryDateEntryView extends LinearLayout {

    private JudoEditText expiryDateEditText;
    private TextInputLayout expiryDateInputLayout;

    public ExpiryDateEntryView(final Context context) {
        super(context);
        initialize(context);
    }

    public ExpiryDateEntryView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ExpiryDateEntryView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(final Context context) {
        setOrientation(LinearLayout.VERTICAL);

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_expiry_date_entry, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        expiryDateEditText = (JudoEditText) findViewById(R.id.expiry_date_edit_text);
        expiryDateInputLayout = (TextInputLayout) findViewById(R.id.expiry_date_input_layout);

        HintFocusListener hintFocusListener = new HintFocusListener(expiryDateEditText, getResources().getString(R.string.date_hint));
        expiryDateEditText.setOnFocusChangeListener(hintFocusListener);

        String dateFormat = getResources().getString(R.string.date_format);
        NumberFormatTextWatcher numberFormatTextWatcher = new NumberFormatTextWatcher(expiryDateEditText, dateFormat);
        expiryDateEditText.addTextChangedListener(numberFormatTextWatcher);
    }

    public void setText(final CharSequence text) {
        expiryDateEditText.setText(text);
    }

    public void addTextChangedListener(final SimpleTextWatcher watcher) {
        expiryDateEditText.addTextChangedListener(watcher);
    }

    public void setExpiryDate(final String expiryDate) {
        expiryDateEditText.setText(expiryDate);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        expiryDateEditText.setEnabled(false);
    }

    public String getText() {
        return expiryDateEditText.getText().toString().trim();
    }

    public JudoEditText getEditText() {
        return expiryDateEditText;
    }

    public void setValidation(final Validation validation) {
        expiryDateInputLayout.setErrorEnabled(validation.isShowError());

        if (validation.isShowError()) {
            expiryDateInputLayout.setError(getResources().getString(validation.getError()));
        } else {
            expiryDateInputLayout.setError("");
        }
    }
}