package com.project.parking_system.service;

import com.project.parking_system.dto.ParkingSessionDto;
import com.project.parking_system.entity.ParkingSessionEntity;
import com.project.parking_system.entity.ParkingSlotEntity;
import com.project.parking_system.entity.VehicleEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for CRUD operations on Parking Sessions.
 */

public interface ParkingSessionService {

    // Help us find if the vehicle that is Given has any Active Session or not.
    Optional<ParkingSessionEntity> findActiveSession(VehicleEntity vehicleEntity);

    // Creates a new Session with Vehicle and Parking Slot Information.
    ParkingSessionEntity createSession(VehicleEntity vehicleEntity, ParkingSlotEntity slot);

    // Helps end the Active Session and Generate the bill.
    void endSession(ParkingSessionEntity session, LocalDateTime exitTime, Double totalAmount);

    // To get all the Active Sessions
    List<ParkingSessionDto> getAllActiveSessions(Long lotId);

    // To get all the Sessions ( BOTH ACTIVE and COMPLETED )
    List<ParkingSessionDto> getAllSessions(Long lotId);

    // To Terminate the Session
    void terminateSession(Long sessionId);
}
