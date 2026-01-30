package com.project.parking_system.dto;

import com.project.parking_system.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request payload for Vehicle Entry.
 * <p>
 * Contains all necessary information required to initiate a parking session.
 * Used in POST /api/parking/entry
 */

@Data
public class EntryRequest {

    // The license plate number of the vehicle.
    @NotBlank(message = "Vehicle Number cannot be empty.")
    private String vehicleNumber;

     //The type of vehicle (CAR or BIKE).
     //Used for classification (and potentially different pricing in future extensions).
    @NotNull(message = "Vehicle Type cannot be empty.")
    private VehicleType vehicleType;

    // The ID of the Parking Lot where the user wants to park
    @NotNull(message = "Parking Lot ID cannot be empty.")
    private Long parkingLotId;
}