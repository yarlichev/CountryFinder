package com.countryfinder.util;

import java.util.Calendar;

public class DateStringConverter {
    public final static String DATE_SEPARATOR_REGEX = "\\.";

    public static String convertToString(Calendar date){
        if(date == null){
            return null;
        }
        return date.get(Calendar.DAY_OF_MONTH) + "." + date.get(Calendar.MONTH)
                + "." + date.get(Calendar.YEAR);
    }

    public static Calendar convertToDate(String date){
        if(date == null || date.isEmpty() || !isADate(date)){
            return null;
        }
        String[] dates = date.split(DATE_SEPARATOR_REGEX);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dates[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dates[1]));
        calendar.set(Calendar.YEAR, Integer.valueOf(dates[2]));
        return calendar;
    }

    public static boolean isADate(String date){
        String[] dates = date.split(DATE_SEPARATOR_REGEX);
        String day = dates[0];
        String month = dates[1];
        String year = dates[2];
        boolean dayIsValid = day.matches("\\d{1,2}")
                && Integer.valueOf(day) >= 1 && Integer.valueOf(day) <= 31;
        boolean monthIsValid = month.matches("\\d{1,2}")
                && Integer.valueOf(month) >= 0 && Integer.valueOf(month) <= 11;
        boolean yearIsValid = year.matches("\\d{4}")
                && Integer.valueOf(year) >= 2024;
        return dayIsValid && monthIsValid && yearIsValid;
    }
}
