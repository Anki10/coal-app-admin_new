package com.anova.indiaadmin.database;

import android.arch.persistence.room.TypeConverter;

import com.anova.indiaadmin.utils.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by iqbal on 7/5/18.
 */

public class DateConverter {
    static DateFormat df = new SimpleDateFormat(Constants.DOB_FORMAT);

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String dateToTimestamp(Date value) {

        return value == null ? null : df.format(value);
    }
}
