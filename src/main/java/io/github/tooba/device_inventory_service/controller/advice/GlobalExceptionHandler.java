package io.github.tooba.device_inventory_service.controller.advice;

import io.github.tooba.device_inventory_service.controller.responseDto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        return new ErrorResponse(
                "VALIDATION_ERROR",
                "Request validation failed",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                Instant.now(),
                fieldErrors
        );
    }

    // 422 - Business Rule Violations
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleBusiness(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                "BUSINESS_RULE_VIOLATION",
                ex.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                request.getRequestURI(),
                Instant.now(),
                null
        );
    }

    // 409 - Conflict (e.g., unique constraint)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                "RESOURCE_CONFLICT",
                "Resource conflict occurred",
                HttpStatus.CONFLICT.value(),
                request.getRequestURI(),
                Instant.now(),
                null
        );
    }

    // 500 - Fallback
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(),
                Instant.now(),
                null
        );
    }
}