package com.project.parking_system.service;

import com.project.parking_system.dto.BillingResult;

import java.time.LocalDateTime;

/**
 * Service interface for calculating parking fees.
 * This encapsulates the "Dynamic Pricing" business logic defined in the requirements.
 */

public interface BillingService {

    // Calculates the final bill amount based on duration and lot occupancy.
    BillingResult calculateBill(LocalDateTime entryTime,
                                LocalDateTime exitTime,
                                Double basePricePerHour,
                                Long parkingLotId,
                                Integer totalSlots);
}
