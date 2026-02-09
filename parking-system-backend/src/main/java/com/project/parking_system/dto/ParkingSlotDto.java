package com.project.parking_system.dto;


import com.project.parking_system.enums.SlotStatus;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for a single Parking Slot.
 * Returns the lightweight status of a slot without exposing
 * relationship overhead (like the full parent Lot object).
 * Used when viewing the grid of slots for a specific lot.
 */

@Data
@Builder
public class ParkingSlotDto {
    private Long id;
    private Integer slotNumber;
    private SlotStatus status;
}
