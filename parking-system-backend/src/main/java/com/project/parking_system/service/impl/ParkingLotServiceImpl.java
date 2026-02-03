package com.project.parking_system.service.impl;

import com.project.parking_system.dto.ParkingLotDTO;
import com.project.parking_system.dto.ParkingLotRequest;
import com.project.parking_system.entity.ParkingLot;
import com.project.parking_system.entity.ParkingSlot;
import com.project.parking_system.enums.SlotStatus;
import com.project.parking_system.exception.BusinessException;
import com.project.parking_system.exception.ResourceNotFoundException;
import com.project.parking_system.mapper.ParkingLotMapper;
import com.project.parking_system.repository.ParkingLotRepository;
import com.project.parking_system.repository.ParkingSlotRepository;
import com.project.parking_system.service.ParkingLotService;
import com.project.parking_system.service.ParkingSlotService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ParkingLot management.
 * Handles the lifecycle of a Parking Facility, including its initial creation
 * and the automatic generation of its internal slots.
 */

@Service
@RequiredArgsConstructor  // Creates a constructor for all final fields (Dependency Injection).
public class ParkingLotServiceImpl implements ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSlotService parkingSlotService;
    private final ParkingSlotRepository parkingSlotRepository;

    //Creates a new lot.
    //Transactional: Ensures that both the Lot and its Slots are saved. If slot generation fails, the Lot is rolled back.
    @Override
    @Transactional
    public ParkingLotDTO createParkingLot(ParkingLotRequest request) {

        // 1. Convert the incoming DTO into an entity we can save.
        ParkingLot parkingLot = ParkingLotMapper.toEntity(request);

        // 2. Save the new ParkingLot entity to the database.
        // After this line, the 'savedLot' object will have its 'id' and 'createdAt' fields populated by the database.
        ParkingLot savedLot = parkingLotRepository.save(parkingLot);

        // 3. Use the parkingSlotService to generate the data for that lot and then store it in that Lot id.
        parkingSlotService.createAndSaveSlotsForLot(savedLot);

        // 4. // Return DTO (Available = Total at start)
        ParkingLotDTO dto = ParkingLotMapper.toDto(savedLot);

        dto.setAvailableSlots(savedLot.getTotalSlots());

        return dto;
    }

    @Override
    public List<ParkingLotDTO> getAllParkingLots() {

        // 1. Fetch all entities from DB
        List<ParkingLot> lots = parkingLotRepository.findAll();

        // 2. Convert List<Entity> -> List<DTO> using Java Streams
        return lots.stream().map(lot -> {
            ParkingLotDTO dto = ParkingLotMapper.toDto(lot);
            // Calculate available slots dynamically
            long available = parkingSlotRepository.countByParkingLotIdAndSlotStatus(lot.getId(), SlotStatus.AVAILABLE);
            dto.setAvailableSlots((int) available);
            return dto;
        }).toList();

    }

    @Override
    public ParkingLotDTO getParkingLotById(Long id) {

        // 1. Try to find the lot. If not found, throw our custom exception.
        ParkingLot currentLot = parkingLotRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Parking lot not found by id " + id)
        );
        ParkingLotDTO dto = ParkingLotMapper.toDto(currentLot);

        // 2. Calculate dynamic availability
        long available = parkingSlotRepository.countByParkingLotIdAndSlotStatus(id, SlotStatus.AVAILABLE);
        dto.setAvailableSlots((int) available);
        return dto;
    }

    /**
     * Updates an existing Parking Lot.
     * CRITICAL BUSINESS LOGIC: Cannot reduce the number of slots for a parking lot.
     */
    @Override
    public ParkingLotDTO updateParkingLot(Long id, ParkingLotRequest request){

    // 1. Find the lot that is coming from the frontend
    ParkingLot currentLot = parkingLotRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Lot not found"));

    // -----Assumption: Lot size cannot be reduced
    if (request.getTotalSlots() < currentLot.getTotalSlots()) throw new BusinessException("Lot size cannot be reduced");

    // 3. Handle Slot Expansion (If new total is higher)
    if (request.getTotalSlots() > currentLot.getTotalSlots()) {
            int startSlot = currentLot.getTotalSlots() + 1;
            int endSlot = request.getTotalSlots();

            List<ParkingSlot> newSlots = new ArrayList<>();
            for (int i = startSlot; i <= endSlot; i++) {
                newSlots.add(ParkingSlot.builder()
                        .slotNumber(i)
                        .slotStatus(SlotStatus.AVAILABLE)
                        .parkingLot(currentLot)
                        .build());
            }
            parkingSlotRepository.saveAll(newSlots);
        }

        // 4. Update Fields
        currentLot.setName(request.getName());
        currentLot.setLocation(request.getLocation());
        currentLot.setBasePricePerHour(request.getBasePricePerHour());
        currentLot.setTotalSlots(request.getTotalSlots());

        // 5. Save & Convert
        ParkingLot savedLot = parkingLotRepository.save(currentLot);

        // Recalculate available slots
        ParkingLotDTO dto = ParkingLotMapper.toDto(savedLot);
        long available = parkingSlotRepository.countByParkingLotIdAndSlotStatus(savedLot.getId(), SlotStatus.AVAILABLE);
        dto.setAvailableSlots((int) available);

        return dto;

    }
}


