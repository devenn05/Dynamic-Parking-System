package com.project.parking_system.dto;

import com.project.parking_system.enums.SessionStatus;
import com.project.parking_system.enums.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Parking Sessions.
 * Used to display lists of vehicles (both currently active and historical records)
 * on the frontend "Sessions" dashboard.
 */

@Data
@Builder
public class ParkingSessionDTO {
    private Long sessionId;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private String parkingLotName;
    private Integer slotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double totalAmount;
    private SessionStatus status;
}
