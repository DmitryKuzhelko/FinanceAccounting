package com.bignerdranch.android.financeaccounting.Utils;

import android.util.Log;

import com.bignerdranch.android.financeaccounting.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    public static String getDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }

    public static String getDateForPeriod(long startDate, long endDate, String period) {
        String date;
        switch (period) {
            case Constants.YEAR:
                SimpleDateFormat dateFormatForYear = new SimpleDateFormat("yyyy"); //example: 2017
                date = dateFormatForYear.format(startDate);
                break;
            case Constants.MONTH:
                SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM yyyy"); //example: December 2017
                date = dateFormatForMonth.format(startDate);
                break;
            case Constants.WEEK:
                SimpleDateFormat dateFormatForWeek0 = new SimpleDateFormat("dd");
                SimpleDateFormat dateFormatForWeek1 = new SimpleDateFormat("dd MMM. yyyy");
                date = dateFormatForWeek0.format(startDate) + " - " + dateFormatForWeek1.format(endDate); //example: 18 - 25 dec. 2017
                break;
            case Constants.DAY:
                SimpleDateFormat dateFormatForDay = new SimpleDateFormat("dd MMMM yyyy");
                date = dateFormatForDay.format(startDate); //example: 25 December 2017
                break;
            case Constants.RANGE:
                SimpleDateFormat dateFormatForRange0 = new SimpleDateFormat("dd MMM yyyy");
                SimpleDateFormat dateFormatForRange1 = new SimpleDateFormat("dd MMM yyyy");
                date = dateFormatForRange0.format(startDate) + " - " + dateFormatForRange1.format(startDate); //example: 18 dec. 2017 - 25 dec. 2017
                break;
            default:
                date = "default";
                break;
        }
        return date;
    }

    public static String getTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
        return timeFormat.format(time);
    }

    public static String getFullDateString(long date) {
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd.MM.yy HH.mm");
        return fullDateFormat.format(date);
    }

    public static long getFullDateLong(String date, String time) {
        long timeInMillis = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH.mm");
            timeInMillis = dateFormat.parse(date + " " + time).getTime();
        } catch (ParseException e) {
            Log.i(e.toString(), "Failed to convert string to long");
            e.printStackTrace();
        }
        return timeInMillis;
    }

    public static void setCurrentTimeAndDate(Calendar calendar) {
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);
    }

    public static long[] getDayTimeRange() {
        Calendar calendar = Calendar.getInstance();
        long[] dayTimeRange = new long[2];
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        dayTimeRange[0] = calendar.getTimeInMillis();
        Log.i("dayTimeRange ", " " + calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        Log.i("dayTimeRange ", " " + calendar.getTime());
        dayTimeRange[1] = calendar.getTimeInMillis();
        Log.i("daysCount ", " " + (dayTimeRange[1] - dayTimeRange[0]) / 86400000);//temporarily
        return dayTimeRange;
    }

    public static long[] getWeekTimeRange() {
        Calendar calendar = Calendar.getInstance();
        long[] weekTimeRange = new long[2];
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        weekTimeRange[0] = calendar.getTimeInMillis();
        Log.i("weekTimeRange ", " " + calendar.getTime());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 7);
        Log.i("weekTimeRange ", " " + calendar.getTime());
        weekTimeRange[1] = calendar.getTimeInMillis();
        Log.i("weekDaysCount ", " " + (weekTimeRange[1] - weekTimeRange[0]) / 86400000);//temporarily
        return weekTimeRange;
    }

    public static long[] getMonthTimeRange() {
        Calendar calendar = Calendar.getInstance();
        long[] monthTimeRange = new long[2];
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        monthTimeRange[0] = calendar.getTimeInMillis();
        Log.i("monthTimeRange ", " " + calendar.getTime());
        calendar.add(Calendar.MONTH, 1);
        Log.i("monthTimeRange ", " " + calendar.getTime());
        monthTimeRange[1] = calendar.getTimeInMillis();
        Log.i("monthDaysCount ", " " + (monthTimeRange[1] - monthTimeRange[0]) / 86400000);//temporarily
        return monthTimeRange;
    }

    public static long[] getAllTimeRange() {    //may be the range of the year ???
        Calendar calendar = Calendar.getInstance();
        long[] yearTimeRange = new long[2];
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        yearTimeRange[0] = calendar.getTimeInMillis();
        Log.i("yearTimeRange ", " " + calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Log.i("yearTimeRange ", " " + calendar.getTime());
        yearTimeRange[1] = calendar.getTimeInMillis();
        Log.i("yearDaysCount ", " " + (yearTimeRange[1] - yearTimeRange[0]) / 86400000);//temporarily
        return yearTimeRange;
    }


    /////////////////
    public static long[] getDay(int shift) {
        Calendar calendar = Calendar.getInstance();
        long[] dayTimeRange = new long[2];

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + shift);
        setTime(calendar);
        Log.i("dayTimeRange ", " " + calendar.getTime());

        dayTimeRange[0] = calendar.getTimeInMillis();

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        Log.i("dayTimeRange ", " " + calendar.getTime());

        dayTimeRange[1] = calendar.getTimeInMillis();

        Log.i("dayTimeRange ", " " + (dayTimeRange[1] - dayTimeRange[0]) / 86400000);//temporarily
        return dayTimeRange;
    }

    public static long[] getWeek(int shift) {
        Calendar calendar = Calendar.getInstance();
        long[] weekTimeRange = new long[2];

        calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + shift);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        setTime(calendar);
        Log.i("weekTimeRange ", " " + calendar.getTime());

        weekTimeRange[0] = calendar.getTimeInMillis();

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 7);
        calendar.add(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND) - 1);
        Log.i("weekTimeRange ", " " + calendar.getTime());

        weekTimeRange[1] = calendar.getTimeInMillis();

        Log.i("weekTimeRange ", " " + (weekTimeRange[1] - weekTimeRange[0]) / 86400000);//temporarily
        return weekTimeRange;
    }

    public static long[] getMonth(int shift) {
        Calendar calendar = Calendar.getInstance();
        long[] monthTimeRange = new long[2];

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + shift);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setTime(calendar);
        Log.i("monthTimeRange ", " " + calendar.getTime());

        monthTimeRange[0] = calendar.getTimeInMillis();

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        Log.i("monthTimeRange ", " " + calendar.getTime());

        monthTimeRange[1] = calendar.getTimeInMillis();

        Log.i("monthDaysCount ", " " + (monthTimeRange[1] - monthTimeRange[0]) / 86400000);//temporarily
        return monthTimeRange;
    }

    public static long[] getYear(int shift) {
        Calendar calendar = Calendar.getInstance();
        long[] yearTimeRange = new long[2];

        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + shift);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        setTime(calendar);
        Log.i("yearTimeRange ", " " + calendar.getTime());

        yearTimeRange[0] = calendar.getTimeInMillis();

        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        Log.i("yearTimeRange ", " " + calendar.getTime());

        yearTimeRange[1] = calendar.getTimeInMillis();

        Log.i("yearTimeRange ", " " + (yearTimeRange[1] - yearTimeRange[0]) / 86400000);//temporarily
        return yearTimeRange;
    }

    public static long[] getRange(int shift) {
        Calendar calendar = Calendar.getInstance();
        long[] timeRange = new long[2];

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setTime(calendar);
        Log.i("timeRange ", " " + calendar.getTime());

        timeRange[0] = calendar.getTimeInMillis();

        calendar.add(Calendar.MONTH, 1);
        Log.i("timeRange ", " " + calendar.getTime());

        timeRange[1] = calendar.getTimeInMillis();

        Log.i("timeRange ", " " + (timeRange[1] - timeRange[0]) / 86400000);//temporarily
        return timeRange;
    }

    public static void setTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}