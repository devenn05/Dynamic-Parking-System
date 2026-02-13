package com.project.notification_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingLotDto {
    private Long id;
    private String name;
    private String location;
    private Integer totalSlots;
    private Integer availableSlots;
    private Double basePricePerHour;
    private LocalDateTime createdAt;
}
