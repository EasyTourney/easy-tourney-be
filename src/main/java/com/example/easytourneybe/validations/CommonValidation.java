package com.example.easytourneybe.validations;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import org.springframework.context.annotation.Bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonValidation {

    public void validatePageAndSize(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new InvalidRequestException("Page must be greater than or equal to zero, and size must be greater than zero");
        }
    }
    public String escapeSpecialCharacters(String keyword) {
        String regex = "([%_])";
        String replacement = "\\\\$1";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(keyword);
        return matcher.replaceAll(replacement);
    }
}
