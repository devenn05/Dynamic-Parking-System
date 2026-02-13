package com.project.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotUpdateDto {
    private String type;            // "ENTRY" or "EXIT"
    private Long lotId;
    private Integer availableSlots;
    private Integer totalSlots;
}
