package com.project.parking_system.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Standardized API Error Responses.
 * This structure is used by the GlobalExceptionHandler to return consistent
 * error details (timestamp, status code, error message) to the frontend.
 */

@Data
@Builder
public class ApiErrorResponseDto {
    private LocalDateTime timestamp;

    // The HTTP Status code (e.g., 400, 404, 500)
    private int status;

    // A short description of the error (e.g., "Resource Not Found").
    private String error;

    // A detailed message explaining the specific issue (e.g., "Parking lot not found with id 5").
    private String message;

    // The API endpoint path where the error occurred.
    private String path;
}
