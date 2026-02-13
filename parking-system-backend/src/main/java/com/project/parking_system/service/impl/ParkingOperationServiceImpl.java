package com.project.parking_system.service.impl;

import com.project.parking_system.dto.*;
import com.project.parking_system.dto.kafka.SessionUpdateDto;
import com.project.parking_system.dto.kafka.SlotUpdateDto;
import com.project.parking_system.entity.ParkingLot;
import com.project.parking_system.entity.ParkingSession;
import com.project.parking_system.entity.ParkingSlot;
import com.project.parking_system.entity.Vehicle;
import com.project.parking_system.enums.SlotStatus;
import com.project.parking_system.exception.BusinessException;
import com.project.parking_system.exception.ResourceNotFoundException;
import com.project.parking_system.mapper.ParkingMapper;
import com.project.parking_system.mapper.ParkingSessionMapper;
import com.project.parking_system.repository.ParkingLotRepository;
import com.project.parking_system.repository.ParkingSlotRepository;
import com.project.parking_system.service.*;
import com.project.parking_system.utils.ParkingUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of the Core Business Logic.
 * This class orchestrates the Entry and Exit workflows, ensuring all business rules
 * (Double booking, Valid exits, Billing, etc.) are enforced.
 */

@Service
@RequiredArgsConstructor
public class ParkingOperationServiceImpl implements ParkingOperationService {

    private final VehicleService vehicleService;
    private final ParkingSlotService parkingSlotService;
    private final ParkingSessionService parkingSessionService;
    private final ParkingLotRepository parkingLotRepository;
    private final BillingService billingService;
    private final ParkingEventProducer eventProducer;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSessionMapper parkingSessionMapper;

    /**
     * Workflow for Vehicle Entry.
     * 1. Validate Lot.
     * 2. Find/Register Vehicle.
     * 3. Ensure no existing Active Session.
     * 4. Find Available Slot (Locks row).
     * 5. Mark Slot Occupied.
     * 6. Create Active Session.
     * @param request The entry request containing vehicle number, type, and lot ID.
     * @return A {@link ParkingTicketDto} confirming the entry.
     * @throws ResourceNotFoundException if the parking lot does not exist.
     * @throws BusinessException if the parking lot is full or the vehicle already has an active session.
     */
    @Override
    @Transactional
    public ParkingTicketDto enterVehicle(EntryRequestDto request){

        // 1. Check if the parking Lot exists.
        ParkingLot currentParkingLot = parkingLotRepository.findById(request.getParkingLotId()).orElseThrow(
                () -> new ResourceNotFoundException("Parking Lot not found")
        );

        // 2. Create or Find the vehicle if present in the request that is coming from frontend
        Vehicle vehicle = vehicleService.findOrCreateVehicle(request.getVehicleNumber(), request.getVehicleType());

        //3. Check If the vehicle is already running an Active Session if yes Throw Error.
        if (parkingSessionService.findActiveSession(vehicle).isPresent()){
            throw new BusinessException("Vehicle with same ID is already under Active Session.");
        }

        // 4. Find the first available Slot in that Parking Lot.
        ParkingSlot currentSlot = parkingSlotService.findFirstAvailableSlot(request.getParkingLotId());
        // Marked as Occupied if parking Slot is Available
        parkingSlotService.markSlotAsOccupied(currentSlot.getId());


        // 5. Create a new Session for current Request. Starting the Session of entered Vehicle.
        ParkingSession newSession = parkingSessionService.createSession(vehicle, currentSlot);

        ParkingSessionDto sessionDto = parkingSessionMapper.convertToSessionDTO(newSession);

        SessionUpdateDto sessionUpdate = SessionUpdateDto.builder()
                .type("SESSION_ENTRY")
                .lotId(request.getParkingLotId())
                .session(sessionDto)
                .build();

        // 6. Trigger Kafka Update for Session updates
        eventProducer.sendUpdate(sessionUpdate);

        // 6. Trigger Kafka Update for Slot updates
        publishSlotUpdate(request.getParkingLotId(), "ENTRY");

        // 7. Return the DTO
        return ParkingMapper.toTicketDTO(newSession);
    }

    /**
     * Workflow for Vehicle Exit.
     * 1. Find Vehicle & Active Session.
     * 2. Validate Time Logic (Exit > Entry).
     * 3. Calculate Bill.
     * 4. Close Session.
     * 5. Free the Slot.
     * @param request The exit request containing the vehicle number.
     * @return A {@link BillDto} with the final calculated charges.
     * @throws ResourceNotFoundException if the vehicle or its active session cannot be found.
     */
    @Override
    @Transactional
    public BillDto exitVehicle(ExitRequestDto request) {
        // 1. Fetching
        Vehicle vehicle = vehicleService.findByVehicleNumber(request.getVehicleNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found."));

        ParkingSession session = parkingSessionService.findActiveSession(vehicle)
                .orElseThrow(() -> new ResourceNotFoundException("No active session found."));

        // To validate if the Vehicle is affiliated with the given Parking Lot only
        Long actualLotId = session.getParkingSlot().getParkingLot().getId();
        Long exitLotId = request.getParkingLotId();

        if (!actualLotId.equals(exitLotId)) throw new BusinessException("Vehicle is not parked in Parking Lot Id: " + request.getParkingLotId());

        // Pre-fetch Data before ending session (Prevents LazyLoad Exceptions)
        Double basePrice = session.getParkingSlot().getParkingLot().getBasePricePerHour();
        Long lotId = session.getParkingSlot().getParkingLot().getId();
        Integer totalSlots = session.getParkingSlot().getParkingLot().getTotalSlots();
        Long slotId = session.getParkingSlot().getId();

        // 2. Calculation
        LocalDateTime exitTime = LocalDateTime.now();
        // UTILS for validation
        long duration = ParkingUtils.calculateDurationInMinutes(session.getEntryTime(), exitTime);

        BillingResultDto billResult = billingService.calculateBill(
                session.getEntryTime(), exitTime, basePrice, lotId, totalSlots
        );

        // 3. Execution
        parkingSessionService.endSession(session, exitTime, billResult.getTotalAmount());

        ParkingSessionDto sessionDto = parkingSessionMapper.convertToSessionDTO(session);

        SessionUpdateDto sessionUpdate = SessionUpdateDto.builder()
                .type("SESSION_END")
                .lotId(lotId)
                .session(sessionDto)
                .build();

        // 4. Trigger Kafka Update for Session Update ---
        eventProducer.sendUpdate(sessionUpdate);

        parkingSlotService.markSlotAsAvailable(slotId);

        // 5. Trigger Kafka Update ---
        publishSlotUpdate(session.getParkingSlot().getParkingLot().getId(), "EXIT");

        // 6. Return DTO (Using Mapper)
        return ParkingMapper.toBillDTO(session, billResult, duration);
    }

    /**
     * Helper to publish the updated slot count to Kafka.
     * Only calculates and sends AFTER the main transaction succeeds.
     */
    private void publishSlotUpdate(Long lotId, String type) {
        // 1. Get total slots for this lot (for UI context)
        long occupiedCount = parkingSlotRepository.countByParkingLotIdAndSlotStatus(lotId, SlotStatus.OCCUPIED);

        // Retrieve lot to get Total
        ParkingLot lot = parkingLotRepository.findById(lotId).orElseThrow();

        int availableCount = lot.getTotalSlots() - (int) occupiedCount;

        // 2. Build the payload
        SlotUpdateDto update = SlotUpdateDto.builder()
                .type(type)
                .lotId(lotId)
                .availableSlots(availableCount)
                .totalSlots(lot.getTotalSlots())
                .build();

        // 3. Fire and forget!
        eventProducer.sendUpdate(update);
    }
}