package com.project.parking_system.repository;

import com.project.parking_system.entity.ParkingSession;
import com.project.parking_system.enums.SessionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing ParkingSession entities.
 * Handles the historical and active records of vehicle visits.
 */

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

     //What it does: Finds a specific active session for a specific vehicle.
     //SQL: SELECT * FROM parking_sessions WHERE vehicle_id = ? AND status = ?
    Optional<ParkingSession> findByVehicleIdAndSessionStatus(Long vehicleId, SessionStatus status);

    // What it does ->  Finds all sessions with a specific status (e.g., all ACTIVE cars system-wide).
    // SQL: SELECT * FROM parking_sessions WHERE SessionStatus = ?
    List<ParkingSession> findBySessionStatus(SessionStatus status);

    // What it does -> Finds all sessions (History + Active) for a specific Parking Lot.
    // Get History (All statuses) for a specific Lot
    List<ParkingSession> findByParkingSlotParkingLotId(Long parkingLotId);

    // What it does: Finds only ACTIVE sessions for a specific Parking Lot.
    // Get Active Only for a specific Lot
    List<ParkingSession> findByParkingSlotParkingLotIdAndSessionStatus(Long parkingLotId, SessionStatus status);

}
