package com.project.parking_system.exception;

import com.project.parking_system.dto.ApiErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized Exception Handling Aspect.
 * Intercepts exceptions thrown anywhere in the Controller/Service layers
 * and converts them into standardized JSON (ApiErrorResponse) with
 * appropriate HTTP status codes.
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Resource Not Found (404)
    // Triggered by: ResourceNotFoundException.
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiErrorResponseDto> handleResourcesException(ResourceNotFoundException e, HttpServletRequest request){
        ApiErrorResponseDto error =  ApiErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Resources Not Found")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 2. To handle Business Rule Violations (409 Conflict)
    // Triggered by: BusinessException (e.g., "Vehicle already has active session").
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiErrorResponseDto> handleBusinessException(BusinessException e, HttpServletRequest request){
        ApiErrorResponseDto error = ApiErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Business Rule Violation")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Handles Optimistic Locking Failures
    // Triggered when @Version check fails during database commit.
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ResponseEntity<ApiErrorResponseDto> handleOptimisticLockException(ObjectOptimisticLockingFailureException e, HttpServletRequest request) {
        ApiErrorResponseDto error = ApiErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Concurrent Modification")
                .message("This session was modified by another transaction. Please try again.")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Handle Unique Constraint Violations (Duplicate Name)
    // Triggered by: SQL Constraint violations (e.g., creating a lot with an existing name).
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiErrorResponseDto> handleDataIntegrityException(DataIntegrityViolationException e, HttpServletRequest request){
        ApiErrorResponseDto error = ApiErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Data Conflict")
                .message("A database constraint was violated. (e.g., Duplicate Entry)")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }


    // 3. Handles Bad Request (400)
    // Triggered if by any case @Valid fails on DTOs (e.g., "Total Slots cannot be empty").
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponseDto> handleBadRequestException(MethodArgumentNotValidException e, HttpServletRequest request){

        // Collect all validation error messages (e.g., "Name cannot be empty")
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiErrorResponseDto error = ApiErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(errors.toString())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

    // 4. Handles 500 - General/Unexpected Errors.
    // Fallback for any unhandled runtime exceptions.
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponseDto> handleGlobalException(Exception e, HttpServletRequest request){
        ApiErrorResponseDto error = ApiErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
