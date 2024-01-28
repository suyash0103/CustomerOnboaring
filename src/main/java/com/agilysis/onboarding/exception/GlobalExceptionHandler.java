package com.agilysis.onboarding.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.agilysis.onboarding.dto.Response;

import java.util.List;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {

    private <T> ResponseEntity<Response<T>> createErrorResponse(String errorMessage, HttpStatus httpStatus, T data) {
        Response<T> response = new Response<>("Failure", errorMessage, data);
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(BindException.class)
    public <T> ResponseEntity<Response<T>> handleBindException(BindException e) {
        return createErrorResponse(e.getBindingResult().getFieldErrors().get(0).getDefaultMessage(),
                HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Response<List<String>>> handleDuplicateEmailException(DuplicateEmailException e) {
        return createErrorResponse(e.getMessage(), HttpStatus.CONFLICT, e.getExistingEmailIds());
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public <T> ResponseEntity<Response<T>> handleDatabaseOperationException(DatabaseOperationException e) {
        return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Response<Set<Long>>> handleCustomerNotFoundException(CustomerNotFoundException e) {
        return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, e.getIds());
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public <T> ResponseEntity<Response<T>> handleIllegalArgumentException(Exception e) {
        return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(InvalidSubscriptionException.class)
    public <T> ResponseEntity<Response<T>> handleInvalidSubscriptionException(InvalidSubscriptionException e) {
        return createErrorResponse(e.getMessage(), HttpStatus.CONFLICT, null);
    }
}
