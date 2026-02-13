package com.project.notification_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingSessionDto {
    private Long sessionId;
    private String vehicleNumber;
    private String vehicleType;
    private String parkingLotName;
    private Integer slotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double totalAmount;
    private String status;
}
