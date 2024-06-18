package com.swd392.group2.kgrill_service.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    public static String formatTimestamp(Date date) {
        return formatTimestamp(date, DEFAULT_DATE_FORMAT);
    }

    public static String formatTimestamp(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
