package com.example.easytourneybe.exceptions;

import com.example.easytourneybe.model.ResponseObject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static ResponseEntity<ResponseObject> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Validation failed");
        ResponseObject responseObject = new ResponseObject(
                false,
                0,
                ""
        );
        responseObject.setErrorMessage(java.util.Collections.singletonMap("ConstraintViolation Error", errorMessage));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObject);
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static ResponseEntity<ResponseObject> handleInvalidPageAndSizeException(InvalidRequestException e) {
        ResponseObject responseObject = new ResponseObject(
                false,
                0,
                ""
        );
        responseObject.setErrorMessage(java.util.Collections.singletonMap("InvalidPageAndSize Error", e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                responseObject
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static ResponseEntity<ResponseObject> handleNotFoundException(NoSuchElementException e) {
        ResponseObject responseObject = new ResponseObject(
                false,
                0,
                ""
        );
        responseObject.setErrorMessage(java.util.Collections.singletonMap("NotFound Error", e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                responseObject
        );
    }
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static ResponseEntity<ResponseObject> handleNullPointerException(NullPointerException e) {
        ResponseObject responseObject = new ResponseObject(
                false,
                0,
                ""
        );
        responseObject.setErrorMessage(java.util.Collections.singletonMap("NullPointer Error", e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                responseObject
        );
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static ResponseEntity<ResponseObject> handleException(Exception e) {
        ResponseObject responseObject = new ResponseObject(
                false,
                0,
                ""
        );
        responseObject.setErrorMessage(java.util.Collections.singletonMap("INTERNAL SERVER ERROR", e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                responseObject
        );
    }


}
