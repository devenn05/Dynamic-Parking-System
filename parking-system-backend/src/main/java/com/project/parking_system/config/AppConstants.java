package com.project.parking_system.config;

/**
 * Centralized Repository for Application Constants.
 * This class eliminates "magic numbers" from the codebase by defining
 * configuration values (Time limits, Pricing Logic, URLs) in a single location.
 * Changing a value here automatically updates the business logic across the system.
 */
public class AppConstants {
    // Prevent instantiation
    private AppConstants() {}

    // Defines which Frontend URL is allowed to access this API (CORS Security).
    public static final String FRONTEND_URL = "https://parking-system-frontend-jyv9.onrender.com";

    // The initial grace period. If a car exits within this time, the cost is 0.
    // Used in BillingServiceImpl.
    public static final int FREE_PARKING_MINUTES = 30;

    // -------------------------------------------------------------------------
    // BUSINESS LOGIC: DYNAMIC PRICING MULTIPLIERS
    // -------------------------------------------------------------------------
    public static final double MULTIPLIER_STANDARD = 1.0;
    public static final double MULTIPLIER_MEDIUM_DEMAND = 1.25;
    public static final double MULTIPLIER_HIGH_DEMAND = 1.5;
}
