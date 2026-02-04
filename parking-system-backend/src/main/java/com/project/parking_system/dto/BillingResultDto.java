package com.project.parking_system.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Helper DTO to carry calculation details from BillingService to the Controller layer.
 */
@Data
@Builder
public class BillingResultDto {
    private Double totalAmount;
    private Double appliedMultiplier;
    private Long billableHours;
}
