package com.project.parking_system.service.impl;

import com.project.parking_system.config.AppConstants;
import com.project.parking_system.dto.BillingResultDto;
import com.project.parking_system.enums.SlotStatus;
import com.project.parking_system.repository.ParkingSlotRepository;
import com.project.parking_system.service.BillingService;
import com.project.parking_system.utils.ParkingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    /**
     * Calculates the final bill amount based on parking duration and current lot occupancy.
     * With Implementation of Dynamic Billing Logic
     * @param entryTime The timestamp of the vehicle's entry.
     * @param exitTime The timestamp of the vehicle's exit.
     * @param basePricePerHour The base hourly rate for the parking lot.
     * @param parkingLotId The ID of the lot, used to calculate occupancy.
     * @param totalSlots The total capacity of the lot.
     * @return A {@link BillingResultDto} DTO containing the total amount, applied multiplier, and billable hours.
     */
    @Override
    public BillingResultDto calculateBill(LocalDateTime entryTime, LocalDateTime exitTime, Double basePricePerHour, Long parkingLotId, Integer totalSlots){

        // UTILS
        long minutes = ParkingUtils.calculateDurationInMinutes(entryTime, exitTime);

        // 2. Occupancy Multiplier Logic
        double multiplier = getOccupancyMultiplier(parkingLotId, totalSlots);

        // If the parking minutes is less than 30 minutes then the session charges no money.
        if (minutes <= AppConstants.FREE_PARKING_MINUTES) {
            return BillingResultDto.builder().totalAmount(0.0)
                    .appliedMultiplier(multiplier).billableHours(0L).build();
        }

        // Subtracting 30minutes of free session
        long chargeableMinutes = minutes - 30;

        // 2. Converting the minutes into Hours
        // Logic: Charge per hour (or part thereof).
        // Example: 61 minutes = 2 hours charged.
        long billableHours = (long) Math.ceil(chargeableMinutes / 60.0);

        // Formula: hours * basePrice * multiplier
        double totalAmount =  billableHours * basePricePerHour * multiplier;

        return BillingResultDto.builder()
                .totalAmount(totalAmount)
                .appliedMultiplier(multiplier)
                .billableHours(billableHours)
                .build();
    }

    /**
     * Helper Function: Helps Determine if there is an Increase in amount depending on the available slots in the parking Lot.
     * @param parkingLotId The ID of the lot.
      * @param totalSlots The total capacity of the lot.
     * @return The calculated price multiplier.
     */
    private double getOccupancyMultiplier(Long parkingLotId, Integer totalSlots){

        // Gets the count of OCCUPIED slots in a Parking Lot
        long occupiedSlots = parkingSlotRepository.countByParkingLotIdAndSlotStatus(parkingLotId, SlotStatus.OCCUPIED);

        // Convert the count of occupied slots into percentage coverage.
        double occupancyPercentage = ((double) occupiedSlots / totalSlots) * 100;

        // Simple if else statements to determine that Percentage increase in total amount.
        if (occupancyPercentage <= 50) return AppConstants.MULTIPLIER_STANDARD;
        else if (occupancyPercentage <= 80) return AppConstants.MULTIPLIER_MEDIUM_DEMAND;
        else return AppConstants.MULTIPLIER_HIGH_DEMAND;


    }
}
