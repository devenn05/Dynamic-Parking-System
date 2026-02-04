package com.project.parking_system.dto;

import com.project.parking_system.enums.VehicleTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the Parking Ticket.
 * This is the response returned immediately after a vehicle successfully enters.
 * It tells the driver which slot has been allocated to them.
 */

@Data
@Builder
public class ParkingTicketDto {
    private Long sessionId;
    private String vehicleNumber;
    private VehicleTypeEnum vehicleTypeEnum;
    private Integer slotNumber;
    private String parkingLotName;
    private LocalDateTime entryTime;
}
