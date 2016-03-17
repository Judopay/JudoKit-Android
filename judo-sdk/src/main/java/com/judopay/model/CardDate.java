package com.judopay.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

public class CardDate {

    private final int month;
    private final int year;

    public CardDate(String cardDate) {
        String splitCardDate = cardDate.replaceAll("/", "");
        this.month = getMonth(splitCardDate);
        this.year = getYear(splitCardDate);
    }

    public int getYear(String year) {
        if (!isValidDate(year)) {
            return 0;
        }

        return 2000 + Integer.parseInt(year.substring(2, 4));
    }

    public int getMonth(String month) {
        if (!isValidDate(month)) {
            return 0;
        }

        return Integer.parseInt(month.substring(0, 2));
    }

    public boolean isBeforeToday() {
        if(year == 0 && month == 0) {
            return false;
        }

        DateTime midnightToday = getTimeAtStartOfDay();
        LocalDate localDate = getLocalDate(month, year);

        return localDate.isBefore(midnightToday.toLocalDate());
    }

    public boolean isAfterToday() {
        if(year == 0 && month == 0) {
            return false;
        }

        LocalDate expiryLocalDate = getLocalDate(month, year);
        DateTime startOfDay = getTimeAtStartOfDay();

        return expiryLocalDate.isAfter(startOfDay.toLocalDate());
    }

    public boolean isInsideAllowedDateRange() {
        DateTime startOfDay = getTimeAtStartOfDay();

        LocalDate localDate = getLocalDate(month, year);
        LocalDate minDate = getLocalDate(month, startOfDay.getYear() - 10);
        LocalDate maxDate = getLocalDate(month, startOfDay.getYear() + 10);

        return localDate.isAfter(minDate) && localDate.isBefore(maxDate);
    }

    private boolean isValidDate(String date) {
        return date.matches("(?:0[1-9]|1[0-2])[0-9]{2}");
    }

    private DateTime getTimeAtStartOfDay() {
        return new DateTime().withTimeAtStartOfDay();
    }

    private LocalDate getLocalDate(int month, int year) {
        return new YearMonth(year, month).toLocalDate(1);
    }

}