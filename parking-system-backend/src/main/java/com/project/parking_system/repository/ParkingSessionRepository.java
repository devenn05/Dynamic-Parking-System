package com.project.parking_system.repository;

import com.project.parking_system.entity.ParkingSessionEntity;
import com.project.parking_system.enums.SessionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing ParkingSession entities.
 * Handles the historical and active records of vehicle visits.
 */

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSessionEntity, Long> {

     //What it does: Finds a specific active session for a specific vehicle.
     //SQL: SELECT * FROM parking_sessions WHERE vehicle_id = ? AND status = ?
    Optional<ParkingSessionEntity> findByVehicleIdAndSessionStatus(Long vehicleId, SessionStatusEnum status);

    // What it does ->  Finds all sessions with a specific status (e.g., all ACTIVE cars system-wide).
    // SQL: SELECT * FROM parking_sessions WHERE SessionStatus = ?
    List<ParkingSessionEntity> findBySessionStatus(SessionStatusEnum status);

    // What it does -> Finds all sessions (History + Active) for a specific Parking Lot.
    // Get History (All statuses) for a specific Lot
    List<ParkingSessionEntity> findByParkingSlotParkingLotId(Long parkingLotId);

    // What it does: Finds only ACTIVE sessions for a specific Parking Lot.
    // Get Active Only for a specific Lot
    List<ParkingSessionEntity> findByParkingSlotParkingLotIdAndSessionStatus(Long parkingLotId, SessionStatusEnum status);

}
