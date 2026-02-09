package com.project.parking_system.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Parking Lot details.
 * <p>
 * This DTO represents the "Read" view of a parking lot. unlike the Request DTO,
 * this includes system-generated fields like ID, creation timestamp,
 * and the calculated 'availableSlots' count.
 */

@Data
@Builder
public class ParkingLotDto {
    private Long id;
    private String name;
    private String location;
    private Integer totalSlots;
    private Integer availableSlots;
    private Double basePricePerHour;
    private LocalDateTime createdAt;

}
