package com.example.easytourneybe.util;

import java.time.LocalDate;
public class DateValidatorUtils {
    public static boolean isBeforeToday(LocalDate inputDate) {
        LocalDate today = LocalDate.now();
        return inputDate.isBefore(today);
    }

    public static boolean isAfterToday(LocalDate inputDate) {
        LocalDate today = LocalDate.now();
        return inputDate.isAfter(today);
    }
}