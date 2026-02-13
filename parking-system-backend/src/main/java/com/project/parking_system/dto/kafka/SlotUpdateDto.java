package com.project.parking_system.dto.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SlotUpdateDto {
    private String type;  // "ENTRY" or "EXIT"
    private Long lotId;
    private Integer availableSlots;
    private Integer totalSlots;
}
