package com.project.parking_system.dto;

import com.project.parking_system.enums.SessionStatusEnum;
import com.project.parking_system.enums.VehicleTypeEnum;
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
public class ParkingSessionDto {
    private Long sessionId;
    private String vehicleNumber;
    private VehicleTypeEnum vehicleTypeEnum;
    private String parkingLotName;
    private Integer slotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double totalAmount;
    private SessionStatusEnum status;
}
