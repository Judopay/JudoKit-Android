package com.judopay.payment.form.date;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;
import android.widget.EditText;

public abstract class DateEditText extends EditText {

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

    @CallSuper
    public boolean isValid() {
        String text = getText().toString();
        return text.matches("(?:0[1-9]|1[0-2])/[0-9]{2}");
    }
}
