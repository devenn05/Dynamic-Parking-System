package com.project.parking_system.controller;

import com.project.parking_system.dto.ParkingSessionDTO;
import com.project.parking_system.service.ParkingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for retrieving Parking Sessions.
 * Allows querying both currently active sessions (cars inside) and historical data.
 * Base Path: /api/sessions/
 */

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/sessions/")
@RequiredArgsConstructor
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;

    // 1. To retrieve all the Active Sessions -> POST http://localhost:8080/api/sessions/active
    @GetMapping("/active")
    ResponseEntity<List<ParkingSessionDTO>> getActiveSessions(@RequestParam(required = false) Long lotId){
        return ResponseEntity.ok(parkingSessionService.getAllActiveSessions(lotId));
    }

    // 2. To retrieve all the Sessions irrespective of being ACTIVE/COMPLETED -> POST http://localhost:8080/api/sessions/history
    @GetMapping("/history")
    ResponseEntity<List<ParkingSessionDTO>> getAllSessions(@RequestParam(required = false) Long lotId){
        return ResponseEntity.ok(parkingSessionService.getAllSessions(lotId));
    }

}
