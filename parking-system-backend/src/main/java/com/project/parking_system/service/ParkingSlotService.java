package com.project.parking_system.service;

import com.project.parking_system.dto.ParkingSlotDTO;
import com.project.parking_system.entity.ParkingLot;
import com.project.parking_system.entity.ParkingSlot;

import java.util.List;

/**
 * Creates all the necessary slots for a newly created parking lot.
 */

public interface ParkingSlotService {
    // 1. Creates and saves the slot
    void createAndSaveSlotsForLot(ParkingLot parkingLot);

    // 2. Get all slots for a specific lot ID
    List<ParkingSlotDTO> getSlotsByParkingLotId(Long parkingLotId);

    // 3. Find the first available slot
    ParkingSlot findFirstAvailableSlot(Long parkingLotId);

    // 4. To mark the slot as Occupied from Available
    void markSlotAsOccupied(Long slotId);

    // 5. To mark the slot as Available.
    void markSlotAsAvailable(Long slotId);
}
