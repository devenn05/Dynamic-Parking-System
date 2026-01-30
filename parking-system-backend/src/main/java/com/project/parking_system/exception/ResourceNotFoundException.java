package com.project.parking_system.exception;

/**
 * Custom Exception for missing data.
 * Thrown when an ID (Lot, Vehicle, Slot) cannot be found in the DB.
 * Maps to HTTP 404 (Not Found).
 */

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
