package com.havranek.todolist.controller;

import com.havranek.todolist.exceptions.EntityNotFound;
import com.havranek.todolist.model.messages.ErrorResponse;
import com.havranek.todolist.model.messages.ErrorResponseValidation;
import com.havranek.todolist.model.messages.ErrorValidationField;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseValidation> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ErrorValidationField> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorValidationField(err.getField(), err.getDefaultMessage()))
                .sorted(Comparator.comparing(ErrorValidationField::field)
                        .thenComparing(ErrorValidationField::message))
                .toList();

        ErrorResponseValidation response = new ErrorResponseValidation(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({IllegalArgumentException.class, DateTimeException.class})
    public ResponseEntity<ErrorResponse> handleBasicExs(RuntimeException ex){
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<ErrorResponse> handleNotExistEntity (EntityNotFound ex){
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String message;

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException &&
                cause.getMessage().contains("Status")) {
            message = "Invalid status. Allowed values: CREATED, IN_PROCESS, COMPLETED, FAILED.";
        } else {
            message = "Invalid input: " + cause.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler({ConstraintViolationException.class, IOException.class, CsvValidationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = "Invalid CSV format";
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
