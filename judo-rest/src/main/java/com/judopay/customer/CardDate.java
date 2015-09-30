package com.judopay.customer;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

}
