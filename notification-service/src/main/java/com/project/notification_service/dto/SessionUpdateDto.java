package com.project.notification_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionUpdateDto {
    private String type;
    private Long lotId;
    private ParkingSessionDto session;
}
