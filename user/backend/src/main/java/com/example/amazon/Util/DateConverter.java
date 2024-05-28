package com.example.amazon.Util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateConverter {

    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
