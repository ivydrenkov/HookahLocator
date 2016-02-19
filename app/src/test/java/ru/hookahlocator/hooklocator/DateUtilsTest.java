package ru.hookahlocator.hooklocator;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.hookahlocator.hooklocator.util.DateUtils;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */

public class DateUtilsTest {
    @Test
    public void test_isWeekend() throws Exception {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        assertEquals("Test monday", DateUtils.isWeekend(calendar), false);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        assertEquals("Test friday", DateUtils.isWeekend(calendar), false);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        assertEquals("Test sunday", DateUtils.isWeekend(calendar), true);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        assertEquals("Test saturday", DateUtils.isWeekend(calendar), true);
    }
}