package com.project.parking_system.service.impl;

import com.project.parking_system.enums.SlotStatus;
import com.project.parking_system.repository.ParkingSlotRepository;
import com.project.parking_system.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Implementation of the Dynamic Billing Logic.
 * Core Rules:
 * 1. First 30 minutes are free.
 * 2. After that, charged per hour (or part thereof).
 * 3. Dynamic Multiplier based on current lot occupancy percentage.
 */

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final ParkingSlotRepository parkingSlotRepository;

    @Override
    public Double calculateBill(LocalDateTime entryTime, LocalDateTime exitTime, Double basePricePerHour, Long parkingLotId, Integer totalSlots){

        // 1. Calculate duration in minutes
        long minutes = Duration.between(entryTime, exitTime).toMinutes();

        // If the parking minutes is less than 30 minutes then the session charges no money.
        if (minutes <= 30) return 0.0;

        // Subtracting 30minutes of free session
        long chargeableMinutes = minutes - 30;

        // 2. Converting the minutes into Hours
        // Logic: Charge per hour (or part thereof).
        // Example: 61 minutes = 2 hours charged.
        long billableHours = (long) Math.ceil(chargeableMinutes / 60.0);

        // OCCUPANCY MULTIPLIER ---
        double multiplier = getOccupancyMultiplier(parkingLotId, totalSlots);

        // Formula: hours * basePrice * multiplier
        return billableHours * basePricePerHour * multiplier;

    }

    // Helper Function: Helps Determine if there is an Increase in amount depending on the available slots in the parking Lot.
    private double getOccupancyMultiplier(Long parkingLotId, Integer totalSlots){

        // Gets the count of OCCUPIED slots in a Parking Lot
        long occupiedSlots = parkingSlotRepository.countByParkingLotIdAndSlotStatus(parkingLotId, SlotStatus.OCCUPIED);

        // Convert the count of occupied slots into percentage coverage.
        double occupancyPercentage = ((double) occupiedSlots / totalSlots) * 100;

        // Simple if else statements to determine that Percentage increase in total amount.
        if (occupancyPercentage <= 50) return 1.0;
        else if (occupancyPercentage <= 80) return 1.25;
        else return 1.5;


    }
}
