package com.judopay.payment.form;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

public class PostcodeEditText extends EditText {

    public PostcodeEditText(Context context) {
        super(context);
    }

    public PostcodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PostcodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PostcodeEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isValid() {
        return getText().length() > 0;
    }
}
