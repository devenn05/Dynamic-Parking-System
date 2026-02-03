package com.project.parking_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request payload for Vehicle Exit.
 * Only the vehicle number is needed, as the system can look up
 * the active session associated with this vehicle.
 * Used in POST /api/parking/exit
 */

@Data
public class ExitRequest {

    // The license plate number of the exiting vehicle.
    // Must correspond to a vehicle currently having an 'ACTIVE' session.
    @NotBlank(message = "Vehicle Number cannot be empty.")
    private String vehicleNumber;
}
