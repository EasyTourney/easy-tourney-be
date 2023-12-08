package com.example.easytourneybe.validations;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import org.springframework.context.annotation.Bean;

public class CommonValidation {

    public void validatePageAndSize(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new InvalidRequestException("Page must be greater than or equal to zero, and size must be greater than zero");
        }
    }
}
