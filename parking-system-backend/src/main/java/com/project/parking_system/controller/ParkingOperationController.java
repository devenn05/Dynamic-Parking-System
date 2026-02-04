package com.project.parking_system.controller;

import com.project.parking_system.dto.BillDto;
import com.project.parking_system.dto.EntryRequestDto;
import com.project.parking_system.dto.ExitRequestDto;
import com.project.parking_system.dto.ParkingTicketDto;
import com.project.parking_system.service.ParkingOperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Parking Operations (Entry and Exit).
 * Handles the core logic for vehicles entering the lot (generating tickets)
 * and exiting the lot (calculating billing).
 * Base Path: /api/parking
 */

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingOperationController {

    private final ParkingOperationService parkingOperationService;

    // 1. Entry point for Vehicle -> POST http://localhost:8080/api/parking/entry
    @PostMapping("/entry")
    ResponseEntity<ParkingTicketDto> enterVehicle(@Valid @RequestBody EntryRequestDto request){
       return ResponseEntity.ok(parkingOperationService.enterVehicle(request));
    }

    // 1. Exit point for Vehicle -> POST http://localhost:8080/api/parking/exit
    @PostMapping("/exit")
    public ResponseEntity<BillDto> exitVehicle(@Valid @RequestBody ExitRequestDto request) {
        return ResponseEntity.ok(parkingOperationService.exitVehicle(request));
    }
}
