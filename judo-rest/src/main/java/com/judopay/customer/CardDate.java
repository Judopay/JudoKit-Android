package com.judopay.customer;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CardDate {

    private int month;
    private int year;

    public CardDate(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public boolean isPastDate() {
        int fullYear = year + 2000;

        GregorianCalendar startDate = new GregorianCalendar(fullYear, month, 1);

        return startDate.before(Calendar.getInstance());
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%02d/%02d", month, year);
    }

}