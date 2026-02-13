package com.project.parking_system.dto.kafka;

import com.project.parking_system.dto.ParkingSessionDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionUpdateDto {
    private String type;
    private Long lotId;
    private ParkingSessionDto session;
}
