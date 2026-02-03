package com.project.parking_system.exception;

/**
 * Custom Exception for logic violations.
 * Thrown when a user action contradicts business rules
 * (e.g., Exit time before Entry time, Duplicate active session).
 * Maps to HTTP 409 (Conflict).
 */

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
