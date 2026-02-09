package com.project.parking_system.mapper;

import com.project.parking_system.dto.ParkingSessionDto;
import com.project.parking_system.entity.ParkingSession;
import org.springframework.stereotype.Component;

/**
 * Utility class for object mapping.
 * Decouples the Internal Database Entities from the External API DTOs.
 * Ensures that changes in the database structure do not strictly require breaking changes
 * in the API contract, and vice versa.
 */

@Component
public class ParkingSessionMapper {

    // Converts Parking Session into ParkingSessionDTO used in Session Operations.
    public ParkingSessionDto convertToSessionDTO(ParkingSession session) {
        return ParkingSessionDto.builder()
                .sessionId(session.getId())
                .vehicleNumber(session.getVehicle().getVehicleNumber())
                .vehicleType(session.getVehicle().getVehicleType())
                .parkingLotName(session.getParkingSlot().getParkingLot().getName())
                .slotNumber(session.getParkingSlot().getSlotNumber())
                .entryTime(session.getEntryTime())
                .exitTime(session.getExitTime())
                .totalAmount(session.getTotalAmount())
                .status(session.getSessionStatus())
                .build();
    }
}
