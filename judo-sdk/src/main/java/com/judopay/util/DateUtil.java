package com.judopay.util;

import java.util.Calendar;
import java.util.Date;

public interface DateUtil {
    Date getDate();

    Calendar getCalendar();

    Long getTimeWithInterval(Calendar calendar, int interval, int timeUnit);
}
