package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;

import java.util.Collection;

import static java.lang.Character.isDigit;

public class NumberFormatTextWatcher implements TextWatcher {

    private final EditText editText;

    private boolean deleting;
    private int start;

    private String format;

    /**
     * @deprecated use {@link NumberFormatTextWatcher#NumberFormatTextWatcher(JudoEditText, String)}
     * instead as fraud identifiers are only reported correctly when {@link JudoEditText} is used
     *
     * @param editText The EditText to watch
     * @param format   Format string
     */
    @Deprecated
    public NumberFormatTextWatcher(final EditText editText, final String format) {
        this.editText = editText;
        this.format = format;
    }

    public NumberFormatTextWatcher(final JudoEditText editText, final String format) {
        this.editText = editText;
        this.format = format;
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        deleting = before == 1;
        this.start = start;
    }

    @Override
    public void afterTextChanged(final Editable string) {
        if (editText instanceof JudoEditText) {
            formatJudoEditText(string);
        } else {
            formatEditText(string);
        }
    }

    private void formatEditText(final Editable string) {
        editText.removeTextChangedListener(this);
        format(string);
        editText.addTextChangedListener(this);
    }

    private void formatJudoEditText(final Editable string) {
        final JudoEditText judoEditText = (JudoEditText) this.editText;

        final Collection<TextWatcher> textWatchers = judoEditText.getTextWatchers();
        judoEditText.removeTextChangedListeners();

        format(string);
        judoEditText.addTextChangedListeners(textWatchers);
    }

    private void format(final Editable string) {
        if (string.length() > 0) {
            for (int i = string.length(); i > 0; i--) {
                if (!isDigit(string.charAt(i - 1)) || ((deleting && i == start) && (isFormatChar(i)))) {
                    string.delete(i - 1, i);
                }
            }

            for (int i = 0; i < getStringEnd(string); i++) {
                if (isFormatChar(i)) {
                    string.insert(i, String.valueOf(format.charAt(i)));
                }
            }
        }
    }

    private boolean isFormatChar(final int index) {
        return index < format.length() && !isDigit(format.charAt(index));
    }

    private int getStringEnd(final Editable string) {
        return string.length() + 1;
    }

    public void setFormat(final String format) {
        if (!this.format.equals(format)) {
            this.format = format;
            // trigger a key event to reformat the text
            editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
        }
    }

}
