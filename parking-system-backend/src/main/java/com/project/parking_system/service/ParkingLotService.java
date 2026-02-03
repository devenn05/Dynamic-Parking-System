package com.project.parking_system.service;

import com.project.parking_system.dto.ParkingLotDTO;
import com.project.parking_system.dto.ParkingLotRequest;

import java.util.List;

/**
 * Service interface for managing Parking Lot metadata.
 */

public interface ParkingLotService {

    // 1. Creates a new parking lot and initializes its slots.
    ParkingLotDTO createParkingLot(ParkingLotRequest request);

    // 2. Retrieves all lots with their live availability counts.
    List<ParkingLotDTO> getAllParkingLots();

    // 3. Retrieves a single lot by ID.
    ParkingLotDTO getParkingLotById(Long id);

    // 4. To update the Lot Information.
    ParkingLotDTO updateParkingLot(Long id, ParkingLotRequest request);
}
