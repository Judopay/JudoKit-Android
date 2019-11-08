package com.judopay.util;

import java.util.Calendar;
import java.util.Date;

public class DefaultDateUtil implements DateUtil {
    @Override
    public Date getDate() {
        return new Date();
    }

    @Override
    public Calendar getCalendar() {
        return Calendar.getInstance();
    }

    @Override
    public Long getTimeWithInterval(Calendar calendar, int interval, int timeUnit) {
        calendar.add(timeUnit, interval);
        return calendar.getTimeInMillis();
    }
}
