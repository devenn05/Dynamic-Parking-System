package com.project.parking_system.mapper;

import com.project.parking_system.dto.BillDto;
import com.project.parking_system.dto.BillingResultDto;
import com.project.parking_system.dto.ParkingTicketDto;
import com.project.parking_system.entity.ParkingSessionEntity;
import org.springframework.stereotype.Component;

/**
 * Utility class for object mapping.
 * Decouples the Internal Database Entities from the External API DTOs.
 * Ensures that changes in the database structure do not strictly require breaking changes
 * in the API contract, and vice versa.
 */

@Component
public class ParkingMapper {

    /**
     * Converts a Parking Session entity into a Parking Ticket DTO while Entry Vehicle.
     */
    public static ParkingTicketDto toTicketDTO(ParkingSessionEntity session) {
        return ParkingTicketDto.builder()
                .sessionId(session.getId())
                .vehicleNumber(session.getVehicleEntity().getVehicleNumber())
                .vehicleTypeEnum(session.getVehicleEntity().getVehicleTypeEnum())
                .slotNumber(session.getParkingSlotEntity().getSlotNumber())
                .parkingLotName(session.getParkingSlotEntity().getParkingLotEntity().getName())
                .entryTime(session.getEntryTime())
                .build();
    }

    /**
     * Converts an Ending Parking Session into a Bill Dto which is used while exit vehicle.
     */
    public static BillDto toBillDTO(ParkingSessionEntity session, BillingResultDto billingResultDTO, long durationMinutes) {
        // We use the Pre-fetched session data
        return BillDto.builder()
                .sessionId(session.getId())
                .vehicleNumber(session.getVehicleEntity().getVehicleNumber())
                .entryTime(session.getEntryTime())
                .exitTime(session.getExitTime())
                .duration(durationMinutes)
                .totalAmount(billingResultDTO.getTotalAmount())
                .parkingLotName(session.getParkingSlotEntity().getParkingLotEntity().getName())
                // Breakdown details
                .basePricePerHour(session.getParkingSlotEntity().getParkingLotEntity().getBasePricePerHour())
                .billableHours(billingResultDTO.getBillableHours())
                .occupancyMultiplier(billingResultDTO.getAppliedMultiplier())
                .build();
    }

}
