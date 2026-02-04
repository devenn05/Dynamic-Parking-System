package com.project.parking_system.service;

import com.project.parking_system.dto.ParkingSlotDto;
import com.project.parking_system.entity.ParkingLotEntity;
import com.project.parking_system.entity.ParkingSlotEntity;

import java.util.List;

/**
 * Creates all the necessary slots for a newly created parking lot.
 */

public interface ParkingSlotService {
    // 1. Creates and saves the slot
    void createAndSaveSlotsForLot(ParkingLotEntity parkingLotEntity);

    // 2. Get all slots for a specific lot ID
    List<ParkingSlotDto> getSlotsByParkingLotId(Long parkingLotId);

    // 3. Find the first available slot
    ParkingSlotEntity findFirstAvailableSlot(Long parkingLotId);

    // 4. To mark the slot as Occupied from Available
    void markSlotAsOccupied(Long slotId);

    // 5. To mark the slot as Available.
    void markSlotAsAvailable(Long slotId);
}
