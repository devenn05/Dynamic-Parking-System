package com.project.parking_system.dto.kafka;

import com.project.parking_system.dto.ParkingLotDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LotUpdateDto {
    private String type;
    private ParkingLotDto lot;
}
