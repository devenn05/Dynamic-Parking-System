package com.project.parking_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Request payload for Vehicle Exit.
 * Only the vehicle number is needed, as the system can look up
 * the active session associated with this vehicle.
 * Used in POST /api/parking/exit
 */

@Data
@Builder
public class ExitRequestDto {

    // The license plate number of the exiting vehicle.
    // Must correspond to a vehicle currently having an 'ACTIVE' session.
    @NotBlank(message = "Vehicle Number cannot be empty.")
    private String vehicleNumber;

    // To Check if user is using exitVehicle Method for valid ParkingLot only
    @NotNull(message = "Parking Lot ID cannot be empty.")
    private Long parkingLotId;
}
