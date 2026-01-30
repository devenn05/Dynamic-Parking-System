package com.project.parking_system.service.impl;

import com.project.parking_system.dto.ParkingSessionDTO;
import com.project.parking_system.entity.ParkingSession;
import com.project.parking_system.entity.ParkingSlot;
import com.project.parking_system.entity.Vehicle;
import com.project.parking_system.enums.SessionStatus;
import com.project.parking_system.repository.ParkingSessionRepository;
import com.project.parking_system.service.ParkingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *Service for handling Session Entities.
 */

@Service
@RequiredArgsConstructor
public class ParkingSessionServiceImpl implements ParkingSessionService {

    private final ParkingSessionRepository parkingSessionRepository;

    // Returns active session or Null by vehicleId
    @Override
    public Optional<ParkingSession> findActiveSession(Vehicle vehicle){
        return parkingSessionRepository.findByVehicleIdAndSessionStatus(vehicle.getId(), SessionStatus.ACTIVE);
    }

    // Creates a new Session in our DB
    @Override
    public ParkingSession createSession(Vehicle vehicle, ParkingSlot slot){
        ParkingSession currentSession = ParkingSession.builder()
                .vehicle(vehicle)
                .parkingSlot(slot)
                .entryTime(LocalDateTime.now())
                .sessionStatus(SessionStatus.ACTIVE)
                .build();

        // Saves the currentSession that we created into DB.
        return parkingSessionRepository.save(currentSession);
    }

    @Override
    public void endSession(ParkingSession session, LocalDateTime exitTime, Double totalAmount){
        // Log the exit time
        session.setExitTime(exitTime);

        // Log the total amount
        session.setTotalAmount(totalAmount);

        // Change the Session Status from Active to Completed
        session.setSessionStatus(SessionStatus.COMPLETED);

        // Save the Session.
        parkingSessionRepository.save(session);
    }

    // Get Parking Sessions by lotId and then find all the Active Session and Give the ParkingSessionDTO for frontend.
    @Override
    public List<ParkingSessionDTO> getAllActiveSessions(Long lotId) {
        List<ParkingSession> sessions;

        // If a specific Lot ID is provided, filter by that Lot AND Status = ACTIVE.
        if (lotId != null) sessions = parkingSessionRepository.findByParkingSlotParkingLotIdAndSessionStatus(lotId, SessionStatus.ACTIVE);

        // If no Lot ID is provided, fetch ALL active vehicles across all parking lots.
        else  sessions = parkingSessionRepository.findBySessionStatus(SessionStatus.ACTIVE);

        //Convert the database entities to DTOs using the helper method
        return sessions.stream().map(this::convertToDTO).toList();
    }

    @Override
    public List<ParkingSessionDTO> getAllSessions(Long lotId){
        List<ParkingSession> sessions;

        // If a specific Lot ID is provided, fetch entire history for that lot.
        if (lotId != null) sessions = parkingSessionRepository.findByParkingSlotParkingLotId(lotId);

        // Global history: Fetch every session ever recorded.
        else sessions = parkingSessionRepository.findAll();

        //Convert the database entities to DTOs using the helper method
        return sessions.stream().map(this::convertToDTO).toList();
    }

    /**
     * // Helper method to Convert the Sessions for sending it to the frontend.
     */
    private ParkingSessionDTO convertToDTO(ParkingSession session) {
        return ParkingSessionDTO.builder()
                .sessionId(session.getId())
                .vehicleNumber(session.getVehicle().getVehicleNumber())
                .vehicleType(session.getVehicle().getVehicleType())
                .slotNumber(session.getParkingSlot().getSlotNumber())
                .entryTime(session.getEntryTime())
                .exitTime(session.getExitTime())
                .totalAmount(session.getTotalAmount())
                .status(session.getSessionStatus())
                .build();
    }
}
