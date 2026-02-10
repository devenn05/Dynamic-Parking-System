package com.project.parking_system.repository;

import com.project.parking_system.BaseTestIT;
import com.project.parking_system.entity.ParkingLot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingLotRepositoryTest extends BaseTestIT {

    @Autowired private ParkingLotRepository parkingLotRepository;
    @Autowired private ParkingSlotRepository parkingSlotRepository;
    @Autowired private ParkingSessionRepository parkingSessionRepository;
    @Autowired private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp(){
        parkingSlotRepository.deleteAll();
        parkingLotRepository.deleteAll();
        parkingSessionRepository.deleteAll();
        vehicleRepository.deleteAll();
    }

    @Test
    void saveParkingLot(){
        ParkingLot currentLot = ParkingLot.builder()
                .name("Test Lot")
                .location("Test Location")
                .basePricePerHour(20.0)
                .totalSlots(50)
                .build();

        ParkingLot savedLot = parkingLotRepository.save(currentLot);

        assertNotNull(savedLot.getId());

        Optional<ParkingLot> fetchedLot = parkingLotRepository.findById(savedLot.getId());

        assertTrue(fetchedLot.isPresent());
        assertEquals("Test Lot", fetchedLot.get().getName());
        assertEquals("Test Location", fetchedLot.get().getLocation());
    }

    @Test
    void duplicatePrevention(){
        ParkingLot lot1 = ParkingLot.builder()
                .name("Duplicate Name") 
                .location("Loc A")
                .totalSlots(10)
                .basePricePerHour(10.0)
                .build();

        ParkingLot lot2 = ParkingLot.builder()
                .name("Duplicate Name") // Same Name
                .location("Loc B")
                .totalSlots(20)
                .basePricePerHour(20.0)
                .build();

        parkingLotRepository.save(lot1);

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> parkingLotRepository.save(lot2));
    }
}
