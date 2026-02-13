package com.project.notification_service.dto;

import lombok.Data;

@Data
public class LotUpdateDto {
    private String type;
    private ParkingLotDto lot;
}
