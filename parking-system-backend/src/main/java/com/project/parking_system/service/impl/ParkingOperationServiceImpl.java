package com.project.parking_system.service.impl;

import com.project.parking_system.dto.BillDTO;
import com.project.parking_system.dto.EntryRequest;
import com.project.parking_system.dto.ExitRequest;
import com.project.parking_system.dto.ParkingTicketDTO;
import com.project.parking_system.entity.ParkingLot;
import com.project.parking_system.entity.ParkingSession;
import com.project.parking_system.entity.ParkingSlot;
import com.project.parking_system.entity.Vehicle;
import com.project.parking_system.exception.BusinessException;
import com.project.parking_system.exception.ResourceNotFoundException;
import com.project.parking_system.repository.ParkingLotRepository;
import com.project.parking_system.service.*;
import com.project.parking_system.service.*;
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
        return ParkingTicketDTO.builder()
                .sessionId(newSession.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .vehicleType(vehicle.getVehicleType())
                .slotNumber(currentSlot.getSlotNumber())
                .parkingLotName(currentParkingLot.getName())
                .entryTime(newSession.getEntryTime()).build();
    }

    /**
     * Workflow for Vehicle Exit.
     * 1. Find Vehicle & Active Session.
     * 2. Validate Time Logic (Exit > Entry).
     * 3. Calculate Bill.
     * 4. Close Session.
     * 5. Free the Slot.
     */
    @Override
    @Transactional
    public BillDTO exitVehicle(ExitRequest request){

        // 1. Find the vehicle if present in the request that is coming from frontend and if not throw an Exception
        Vehicle vehicle = vehicleService.findByVehicleNumber(request.getVehicleNumber()).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found in Parking Lot."));

        // 2. Find if Session is Active with respect to that vehicle.
        ParkingSession currentSession = parkingSessionService.findActiveSession(vehicle).orElseThrow(() -> new ResourceNotFoundException("No active parking session found for this vehicle"));

        // 3. Save the current time which will be our exit time.
        LocalDateTime currTime = LocalDateTime.now();

        // ---------IMPORTANT: If Exit Time is Invalid (E.g. exit before entry)---------------
        if (currTime.isBefore(currentSession.getEntryTime())) {
            throw new BusinessException("Invalid exit time. Exit cannot be before entry.");
        }

        // 4. Find the base price for that Session.
        Double basePrice = currentSession.getParkingSlot().getParkingLot().getBasePricePerHour();

        // Preventing Entry and Exit in a same minute.
        long totalDuration = Duration.between(currentSession.getEntryTime(), currTime).toSeconds();
        if (totalDuration < 60) throw new BusinessException("Exit not allowed within the same minute of entry.");


        // 5. Find the total amount for that slot with entry and exit in Calculations.
        Double totalAmount = billingService.calculateBill(currentSession.getEntryTime(), currTime, basePrice, currentSession.getParkingSlot().getParkingLot().getId(), currentSession.getParkingSlot().getParkingLot().getTotalSlots());

        // 6. End the Session so that the changes will reflect in the Session DB.
        parkingSessionService.endSession(currentSession, currTime, totalAmount);

        // 7. Marks the slot as available for further use.
        parkingSlotService.markSlotAsAvailable(currentSession.getParkingSlot().getId());

        // 8. Store the duration in minutes which will be useful for sending the duration to frontend.
        long durationMinutes = Duration.between(currentSession.getEntryTime(), currTime).toMinutes();

        // 9. Return the DTO which will be going to frontend.
        return BillDTO.builder()
                .sessionId(currentSession.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .entryTime(currentSession.getEntryTime())
                .exitTime(currTime)
                .duration(durationMinutes)
                .totalAmount(totalAmount)
                .parkingLotName(currentSession.getParkingSlot().getParkingLot().getName())
                .build();



    }
}
