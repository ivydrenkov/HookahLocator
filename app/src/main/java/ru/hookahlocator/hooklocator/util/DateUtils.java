package ru.hookahlocator.hooklocator.util;

import java.util.Calendar;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class DateUtils {

    public static boolean isWeekend(Calendar calendar) {
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if ((weekDay == Calendar.SATURDAY)||(weekDay == Calendar.SUNDAY)){
            return true;
        }
        return false;
    }

}
