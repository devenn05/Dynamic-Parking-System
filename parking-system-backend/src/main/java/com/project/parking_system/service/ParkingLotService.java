package com.project.parking_system.service;

import com.project.parking_system.dto.ParkingLotDto;
import com.project.parking_system.dto.ParkingLotRequestDto;

import java.util.List;

/**
 * Service interface for managing Parking Lot metadata.
 */

public interface ParkingLotService {

    // 1. Creates a new parking lot and initializes its slots.
    ParkingLotDto createParkingLot(ParkingLotRequestDto request);

    // 2. Retrieves all lots with their live availability counts.
    List<ParkingLotDto> getAllParkingLots();

    // 3. Retrieves a single lot by ID.
    ParkingLotDto getParkingLotById(Long id);

    // 4. To update the Lot Information.
    ParkingLotDto updateParkingLot(Long id, ParkingLotRequestDto request);

    // 5. To delete a Lot
    void deleteParkingLot(Long id);
}
