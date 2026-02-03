package com.project.parking_system.service.impl;

import com.project.parking_system.dto.*;
import com.project.parking_system.entity.ParkingLot;
import com.project.parking_system.entity.ParkingSession;
import com.project.parking_system.entity.ParkingSlot;
import com.project.parking_system.entity.Vehicle;
import com.project.parking_system.exception.BusinessException;
import com.project.parking_system.exception.ResourceNotFoundException;
import com.project.parking_system.mapper.ParkingMapper;
import com.project.parking_system.repository.ParkingLotRepository;
import com.project.parking_system.service.*;
import com.project.parking_system.service.*;
import com.project.parking_system.utils.ParkingUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    /**
     * Workflow for Vehicle Entry.
     * 1. Validate Lot.
     * 2. Find/Register Vehicle.
     * 3. Ensure no existing Active Session.
     * 4. Find Available Slot (Locks row).
     * 5. Mark Slot Occupied.
     * 6. Create Active Session.
     * @param request The entry request containing vehicle number, type, and lot ID.
     * @return A {@link ParkingTicketDTO} confirming the entry.
     * @throws ResourceNotFoundException if the parking lot does not exist.
     * @throws BusinessException if the parking lot is full or the vehicle already has an active session.
     */
    @Override
    @Transactional
    public ParkingTicketDTO enterVehicle(EntryRequest request){

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

        // 6. Return the DTO
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
     * @return A {@link BillDTO} with the final calculated charges.
     * @throws ResourceNotFoundException if the vehicle or its active session cannot be found.
     */
    @Override
    @Transactional
    public BillDTO exitVehicle(ExitRequest request) {
        // 1. Fetching
        Vehicle vehicle = vehicleService.findByVehicleNumber(request.getVehicleNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found."));

        ParkingSession session = parkingSessionService.findActiveSession(vehicle)
                .orElseThrow(() -> new ResourceNotFoundException("No active session found."));

        // Pre-fetch Data before ending session (Prevents LazyLoad Exceptions)
        Double basePrice = session.getParkingSlot().getParkingLot().getBasePricePerHour();
        Long lotId = session.getParkingSlot().getParkingLot().getId();
        Integer totalSlots = session.getParkingSlot().getParkingLot().getTotalSlots();
        Long slotId = session.getParkingSlot().getId();

        // 2. Calculation
        LocalDateTime exitTime = LocalDateTime.now();
        // UTILS for validation
        long duration = ParkingUtils.calculateDurationInMinutes(session.getEntryTime(), exitTime);

        BillingResult billResult = billingService.calculateBill(
                session.getEntryTime(), exitTime, basePrice, lotId, totalSlots
        );

        // 3. Execution
        parkingSessionService.endSession(session, exitTime, billResult.getTotalAmount());
        parkingSlotService.markSlotAsAvailable(slotId);

        // 4. Return DTO (Using Mapper)
        return ParkingMapper.toBillDTO(session, billResult, duration);
    }
}