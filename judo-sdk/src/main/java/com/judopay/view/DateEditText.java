package com.judopay.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * A view that allows for card dates to be entered and formatted during entry (MM/YY).
 */
public class DateEditText extends AppCompatEditText {

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

    private void initialise() {
        addTextChangedListener(new DateSeparatorTextWatcher(this));
    }

}