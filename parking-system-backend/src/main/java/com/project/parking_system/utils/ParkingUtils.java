package com.project.parking_system.utils;

import com.project.parking_system.exception.BusinessException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Shared Utility Methods.
 */

public class ParkingUtils {

    /**
     * Normalizes a vehicle license plate number. Prevents duplicates caused by formatting differences.
     * Example: "mh-12 ab 1234" becomes "MH12AB1234".
     */
    public static String normalizeVehicleNumber(String input) {
        if (input == null) return null;
        return input.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    /**
     * Calculates the duration in minutes between two timestamps.
     * Validates that the exit time is chronologically after the entry time.
     */
    public static long calculateDurationInMinutes(LocalDateTime entry, LocalDateTime exit) {
        if (exit.isBefore(entry)) {
            throw new BusinessException("Invalid Exit Time: Exit cannot be before Entry.");
        }
        return Duration.between(entry, exit).toMinutes();
    }
}
