package com.project.parking_system.dto;

import com.project.parking_system.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

/**
 * Request payload for Vehicle Entry.
 * <p>
 * Contains all necessary information required to initiate a parking session.
 * Used in POST /api/parking/entry
 */

@Data
@Builder
public class EntryRequestDto {

    // The license plate number of the vehicle.
    // Added Strict Regex Pattern, It rejects special characters, spaces, or invalid structures and maintain Normal Vehicle Number Pattern
    // For both type of Number Plats, old and new BH Plates.
    @NotBlank(message = "Vehicle Number cannot be empty.")
    @Pattern(
            regexp = "^([A-Z]{2}[0-9]{2}[A-Z]{0,3}[0-9]{4}|[0-9]{2}BH[0-9]{4}[A-Z]{1,2})$",
            message = "Invalid format. Use standard (MH12AB1234) or BH (22BH1234AA) format without spaces."
    )
    private String vehicleNumber;

     //The type of vehicle (CAR or BIKE).
     //Used for classification (and potentially different pricing in future extensions).
    @NotNull(message = "Vehicle Type cannot be empty.")
    private VehicleType vehicleType;

    // The ID of the Parking Lot where the user wants to park
    @NotNull(message = "Parking Lot ID cannot be empty.")
    private Long parkingLotId;
}