package com.judopay.payment.form;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

public class IssueNumberEditText extends EditText {

    public IssueNumberEditText(Context context) {
        super(context);
    }

    public IssueNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IssueNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IssueNumberEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isValid() {
        String text = getText().toString();
        try {
            int issueNumber = Integer.parseInt(text);
            return issueNumber > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}