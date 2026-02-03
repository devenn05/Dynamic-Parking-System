package com.project.parking_system.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the final Bill/Receipt.
 * This is returned to the client when a vehicle exits the parking lot.
 * It contains the cost breakdown and timing details.
 */

@Data
@Builder
public class BillDTO {
    private Long sessionId;
    private String vehicleNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Long duration;
    private Double totalAmount;
    private String parkingLotName;
    private Double basePricePerHour;
    private Double occupancyMultiplier;
    private Long billableHours;
}
