package com.example.easytourneybe.exceptions;

import com.example.easytourneybe.model.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseObject handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errMap.put(e.getField(), e.getDefaultMessage()));
        return ResponseObject.builder().errorMessage(errMap).success(false).build();
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ResponseObject> handleAuthenticationException(AuthenticationException ex) {
        Map<String, String> errMessage = new HashMap<>();
        errMessage.put("Authentication Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder().errorMessage(errMessage).success(false).build());
    }
}
