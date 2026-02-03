package com.project.parking_system.service.impl;

import com.project.parking_system.dto.ParkingSessionDTO;
import com.project.parking_system.entity.ParkingSession;
import com.project.parking_system.entity.ParkingSlot;
import com.project.parking_system.entity.Vehicle;
import com.project.parking_system.enums.SessionStatus;
import com.project.parking_system.exception.ResourceNotFoundException;
import com.project.parking_system.mapper.ParkingSessionMapper;
import com.project.parking_system.repository.ParkingSessionRepository;
import com.project.parking_system.service.ParkingSessionService;
import com.project.parking_system.service.ParkingSlotService;
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

    private final ParkingSlotService parkingSlotService;
    private final ParkingSessionMapper parkingSessionMapper;

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
        return sessions.stream().map(parkingSessionMapper::convertToSessionDTO).toList();
    }

    // Get all the Sessions that are related the LotID
    @Override
    public List<ParkingSessionDTO> getAllSessions(Long lotId){
        List<ParkingSession> sessions;

        // If a specific Lot ID is provided, fetch entire history for that lot.
        if (lotId != null) sessions = parkingSessionRepository.findByParkingSlotParkingLotId(lotId);

        // Global history: Fetch every session ever recorded.
        else sessions = parkingSessionRepository.findAll();

        //Convert the database entities to DTOs using the helper method
        return sessions.stream().map(parkingSessionMapper::convertToSessionDTO).toList();
    }

    /**
     * To Forcefully terminates an Active session.
     * Usage: Used when a user exits without generating a bill (e.g., system glitch, lost ticket) or for data cleanup.
     * Side Effects:
     * 1. Frees the Parking Slot immediately.
     * 2. Sets the bill amount to 0.0 (Administrative Override).
     * 3. Sets status to TERMINATED (distinct from COMPLETED).
     */
    @Override
    public void terminateSession(Long sessionId){
        // Find if the Session is present with the Incoming SessionId
        ParkingSession currentSession = parkingSessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("No Session Found with this Session Id."));

        // Checks if the session is Active or not to b terminated.
        if (currentSession.getSessionStatus() != SessionStatus.ACTIVE){
            throw new ResourceNotFoundException("Session is Not active to be Terminated.");
        }

        // Marks the Slot Available from Occupied.
        Long slotId = currentSession.getParkingSlot().getId();
        parkingSlotService.markSlotAsAvailable(slotId);

        // 4. Close the Session
        currentSession.setExitTime(LocalDateTime.now());
        currentSession.setSessionStatus(SessionStatus.TERMINATED);

        // We set it as 0.0 for administrative fix.
        currentSession.setTotalAmount(0.0);

        // Save the Session.
        parkingSessionRepository.save(currentSession);

    }
}
