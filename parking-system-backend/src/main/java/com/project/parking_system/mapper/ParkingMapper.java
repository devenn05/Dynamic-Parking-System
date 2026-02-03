package com.project.parking_system.mapper;

import com.project.parking_system.dto.BillDTO;
import com.project.parking_system.dto.BillingResult;
import com.project.parking_system.dto.ParkingTicketDTO;
import com.project.parking_system.entity.ParkingSession;
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
    public static ParkingTicketDTO toTicketDTO(ParkingSession session) {
        return ParkingTicketDTO.builder()
                .sessionId(session.getId())
                .vehicleNumber(session.getVehicle().getVehicleNumber())
                .vehicleType(session.getVehicle().getVehicleType())
                .slotNumber(session.getParkingSlot().getSlotNumber())
                .parkingLotName(session.getParkingSlot().getParkingLot().getName())
                .entryTime(session.getEntryTime())
                .build();
    }

    /**
     * Converts an Ending Parking Session into a Bill Dto which is used while exit vehicle.
     */
    public static BillDTO toBillDTO(ParkingSession session, BillingResult billingResult, long durationMinutes) {
        // We use the Pre-fetched session data
        return BillDTO.builder()
                .sessionId(session.getId())
                .vehicleNumber(session.getVehicle().getVehicleNumber())
                .entryTime(session.getEntryTime())
                .exitTime(session.getExitTime())
                .duration(durationMinutes)
                .totalAmount(billingResult.getTotalAmount())
                .parkingLotName(session.getParkingSlot().getParkingLot().getName())
                // Breakdown details
                .basePricePerHour(session.getParkingSlot().getParkingLot().getBasePricePerHour())
                .billableHours(billingResult.getBillableHours())
                .occupancyMultiplier(billingResult.getAppliedMultiplier())
                .build();
    }

}
