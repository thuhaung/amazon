package com.example.amazon.Util;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    public static Date getDate30DaysFromNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 30);

        return calendar.getTime();
    }
}
