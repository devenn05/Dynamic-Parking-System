package com.project.parking_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

/**
 * Request payload for creating a new Parking Lot.
 * Validates inputs to ensure logical data integrity (e.g., cannot have negative slots).
 * Used in POST /api/parking-lots
 */

@Data
@Builder
public class ParkingLotRequestDto {

    @NotBlank(message = "Name cannot be empty.")
    private String name;

    @NotBlank(message = "Location cannot be empty.")
    private String location;

    // Total capacity of the lot.
    //  Must be at least 1.
    @Min(value = 1, message = "Total slots must be at least 1")
    @NotNull(message = "Total Slots cannot be empty.")
    private Integer totalSlots;


    @Positive(message = "Price should be positive")
    @NotNull(message = "Base Price cannot be empty.")
    private Double basePricePerHour;
}
