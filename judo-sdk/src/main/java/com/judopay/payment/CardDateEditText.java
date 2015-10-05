package com.judopay.payment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

public class CardDateEditText extends EditText {

    public CardDateEditText(Context context) {
        super(context);
        initialise();
    }

    public CardDateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    public CardDateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardDateEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialise();
    }

    public boolean isValid() {
        String text = getText().toString();
        return text.matches("(?:0[1-9]|1[0-2])/[0-9]{2}");
    }

    private void initialise() {
        setHint("MM/YY");
    }

}
