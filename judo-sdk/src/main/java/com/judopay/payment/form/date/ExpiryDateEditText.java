package com.judopay.payment.form.date;

import android.content.Context;
import android.util.AttributeSet;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

public class ExpiryDateEditText extends DateEditText {

    public ExpiryDateEditText(Context context) {
        super(context);
    }

    public ExpiryDateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpiryDateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ExpiryDateEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean isValid() {
        return super.isValid() && isExpiryDateValid();
    }

    private boolean isExpiryDateValid() {
        DateTime midnightToday = new DateTime().withTimeAtStartOfDay();

        int year = 2000 + Integer.parseInt(getText().toString().substring(3, 5));
        int month = Integer.parseInt(getText().toString().substring(0, 2));

        LocalDate monthAndYear = new YearMonth(year, month).toLocalDate(1);

        return monthAndYear.isAfter(midnightToday.toLocalDate());
    }

}
