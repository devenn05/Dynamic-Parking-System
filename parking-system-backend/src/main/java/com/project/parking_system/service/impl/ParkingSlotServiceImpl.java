package com.project.parking_system.service.impl;

import com.project.parking_system.dto.ParkingSlotDto;
import com.project.parking_system.entity.ParkingLotEntity;
import com.project.parking_system.entity.ParkingSlotEntity;
import com.project.parking_system.enums.SlotStatusEnum;
import com.project.parking_system.exception.BusinessException;
import com.project.parking_system.exception.ResourceNotFoundException;
import com.project.parking_system.repository.ParkingSlotRepository;
import com.project.parking_system.service.ParkingSlotService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service Implementation for Physical Slot management.
 */

@Service
@Transactional
@RequiredArgsConstructor // Creates a constructor for all final fields (Dependency Injection).
public class ParkingSlotServiceImpl implements ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;

    @Override
    public void createAndSaveSlotsForLot(ParkingLotEntity parkingLotEntity){

        // Create an empty list to hold our new slot objects.
        List<ParkingSlotEntity> slots = new ArrayList<>();

        // For each number, create a new ParkingSlot object.
        for (int i = 1; i <= parkingLotEntity.getTotalSlots(); i++){
            ParkingSlotEntity newSlot = ParkingSlotEntity.builder()
                    .slotNumber(i)                          // Set the slot number.
                    .slotStatusEnum(SlotStatusEnum.AVAILABLE)       // All new slots are initially available.
                    .parkingLotEntity(parkingLotEntity)                 // Link it to the parent parking lot.
                    .build();

            slots.add(newSlot);
        }
        // Save all the new slots to the database in one single, efficient operation.
        parkingSlotRepository.saveAll(slots);
    }

    @Override
    public List<ParkingSlotDto> getSlotsByParkingLotId(Long parkingLotId){

        // 1. Fetch slots from DB using the method you defined in your Repository
        List<ParkingSlotEntity> currSlots = parkingSlotRepository.findByParkingLotIdOrderBySlotNumberAsc(parkingLotId);

        // 2. Convert to DTO
        return currSlots.stream().map(s -> ParkingSlotDto.builder()
                                                    .id(s.getId())
                                                    .slotNumber(s.getSlotNumber())
                                                    .status(s.getSlotStatusEnum())
                                                    .build()).toList();
    }

    // The core slot allocation logic.
    @Override
    public ParkingSlotEntity findFirstAvailableSlot(Long parkingLotId){
        // Fetch all available slots for this lot, ordered by number (1, 2, 3...)
        List<ParkingSlotEntity> availableSlots = parkingSlotRepository
                .findByParkingLotIdAndSlotStatusOrderBySlotNumberAsc(parkingLotId, SlotStatusEnum.AVAILABLE);

        // If it is empty then that means there are no empty slots available in our current Parking lot.
        if (availableSlots.isEmpty()){
            throw new BusinessException("Parking lot is full ");
        }
        // Return the first one (e.g., Slot 1)
        return availableSlots.get(0);
    }

    @Override
    public void markSlotAsOccupied(Long slotId){
        // See to it that the Current Slot ID that is past is present or not.
        ParkingSlotEntity currentSlot = parkingSlotRepository.findById(slotId).orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        // If present then this will switch the status from Available to Occupied.
        currentSlot.setSlotStatusEnum(SlotStatusEnum.OCCUPIED);

        // Save that Slot.
        parkingSlotRepository.save(currentSlot);

    }

    @Override
    public void markSlotAsAvailable(Long slotId){
        // See to it that the Current Slot ID that is past is present or not.
        ParkingSlotEntity currentSlot = parkingSlotRepository.findById(slotId).orElseThrow(() -> new ResourceNotFoundException("Invalid Slot Id"));

        // If present then this will switch the status from Occupied to Available.
        currentSlot.setSlotStatusEnum(SlotStatusEnum.AVAILABLE);

        // Save that Slot.
        parkingSlotRepository.save(currentSlot);
    }
}
