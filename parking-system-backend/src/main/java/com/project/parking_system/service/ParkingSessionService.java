package com.project.parking_system.service;

import com.project.parking_system.dto.ParkingSessionDTO;
import com.project.parking_system.entity.ParkingSession;
import com.project.parking_system.entity.ParkingSlot;
import com.project.parking_system.entity.Vehicle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for CRUD operations on Parking Sessions.
 */

public interface ParkingSessionService {

    // Help us find if the vehicle that is Given has any Active Session or not.
    Optional<ParkingSession> findActiveSession(Vehicle vehicle);

    // Creates a new Session with Vehicle and Parking Slot Information.
    ParkingSession createSession(Vehicle vehicle, ParkingSlot slot);

    // Helps end the Active Session and Generate the bill.
    void endSession(ParkingSession session, LocalDateTime exitTime, Double totalAmount);

    // To get all the Active Sessions
    List<ParkingSessionDTO> getAllActiveSessions(Long lotId);

    // To get all the Sessions ( BOTH ACTIVE and COMPLETED )
    List<ParkingSessionDTO> getAllSessions(Long lotId);

    // To Terminate the Session
    void terminateSession(Long sessionId);
}
