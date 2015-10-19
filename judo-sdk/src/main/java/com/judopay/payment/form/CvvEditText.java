package com.judopay.payment.form;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;

import com.judopay.customer.CardType;

public class CvvEditText extends EditText {

    public CvvEditText(Context context) {
        super(context);
    }

    public CvvEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CvvEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CvvEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isValid() {
        return true;
    }

    public void setCardType(int cardType) {
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(cardType == CardType.AMEX ? 4 : 3)});
    }

}