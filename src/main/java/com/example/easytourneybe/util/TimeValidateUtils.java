package com.example.easytourneybe.util;

import com.example.easytourneybe.exceptions.InvalidRequestException;

import java.time.LocalTime;

public class TimeValidateUtils {
    public static LocalTime ParseStringToTime(String time, String message){
        try {
            return LocalTime.parse(time.trim());
        } catch (Exception e) {
            throw new InvalidRequestException(message);
        }
    }
}
