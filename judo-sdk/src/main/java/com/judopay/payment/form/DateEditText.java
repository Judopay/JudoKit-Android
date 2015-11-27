package com.judopay.payment.form;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

public class DateEditText extends EditText {

    public DateEditText(Context context) {
        super(context);
        initialise();
    }

    public DateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    public DateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DateEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialise();
    }

    private void initialise() {
        addTextChangedListener(new DateSeparatorTextWatcher(this));
    }

}