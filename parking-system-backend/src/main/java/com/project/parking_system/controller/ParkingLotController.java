package com.project.parking_system.controller;

import com.project.parking_system.dto.ParkingLotDTO;
import com.project.parking_system.dto.ParkingLotRequest;
import com.project.parking_system.dto.ParkingSlotDTO;
import com.project.parking_system.service.ParkingLotService;
import com.project.parking_system.service.ParkingSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST Controller for managing Parking Lots.
 * Provides endpoints to create parking lots, retrieve them, and fetch their specific slots.
 * Base Path: /api/parking-lots
 */


@RestController
@RequestMapping("/api/parking-lots") // All URLs here start with /api/parking-lots
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService parkingLotService;
    private final ParkingSlotService parkingSlotService;

    // 1. Create a Parking Lot -> POST http://localhost:8080/api/parking-lots
    @PostMapping
    public ResponseEntity<ParkingLotDTO> createParkingLot(@Valid @RequestBody ParkingLotRequest request){

        ParkingLotDTO currentLot = parkingLotService.createParkingLot(request);

        // Return 201 Created status
        return ResponseEntity.created(URI.create("/api/parking-lots/" + currentLot.getId())).body(currentLot);
    }

    // 2. Retrieves All Parking Lots
    // GET http://localhost:8080/api/parking-lots
    @GetMapping
    public ResponseEntity<List<ParkingLotDTO>> getAllParkingLot(){
        return ResponseEntity.ok(parkingLotService.getAllParkingLots());
    }

    // 3. Retrieves a specific Parking Lot by ID
    // GET http://localhost:8080/api/parking-lots/1
    @GetMapping("/{id}")
    public ResponseEntity<ParkingLotDTO> getParkingLotById(@PathVariable Long id){
        return ResponseEntity.ok(parkingLotService.getParkingLotById(id));
    }

    // 4. specific All the Slots for a specific Parking Lot
    // GET http://localhost:8080/api/parking-lots/1/slots
    @GetMapping("/{id}/slots")
    public ResponseEntity<List<ParkingSlotDTO>> getSlotsByLotId(@PathVariable Long id){
        return ResponseEntity.ok(parkingSlotService.getSlotsByParkingLotId(id));
    }

    // 5. Update Parking Lot
    // PUT http://localhost:8080/api/parking-lots/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ParkingLotDTO> updateParkingLot(@PathVariable Long id, @Valid @RequestBody ParkingLotRequest request) {
        return ResponseEntity.ok(parkingLotService.updateParkingLot(id, request));
    }
}
