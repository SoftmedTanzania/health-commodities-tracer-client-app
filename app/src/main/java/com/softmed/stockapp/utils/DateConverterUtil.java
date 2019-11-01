package com.softmed.stockapp.utils;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Created by coze on 11/28/17.
 */

public class DateConverterUtil {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}