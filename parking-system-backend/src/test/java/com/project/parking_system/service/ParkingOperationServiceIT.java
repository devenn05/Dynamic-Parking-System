package com.project.parking_system.service;

import com.project.parking_system.BaseTestIT;
import com.project.parking_system.dto.*;
import com.project.parking_system.entity.ParkingLot;
import com.project.parking_system.entity.ParkingSession;
import com.project.parking_system.entity.ParkingSlot;
import com.project.parking_system.entity.Vehicle;
import com.project.parking_system.enums.SessionStatus;
import com.project.parking_system.enums.SlotStatus;
import com.project.parking_system.enums.VehicleType;
import com.project.parking_system.repository.ParkingLotRepository;
import com.project.parking_system.repository.ParkingSessionRepository;
import com.project.parking_system.repository.ParkingSlotRepository;
import com.project.parking_system.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.trilead.ssh2.Session;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingOperationServiceIT extends BaseTestIT {


    @Autowired private ParkingLotRepository parkingLotRepository;
    @Autowired private ParkingLotService parkingLotService;
    @Autowired private ParkingOperationService parkingOperationService;
    @Autowired private ParkingSlotRepository parkingSlotRepository;
    @Autowired private ParkingSessionRepository parkingSessionRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private ParkingSessionService parkingSessionService;

    @BeforeEach
    void setUp(){
        parkingLotRepository.deleteAll();
        parkingSlotRepository.deleteAll();
        parkingSessionRepository.deleteAll();
        vehicleRepository.deleteAll();
    }

    @Test
    void parkingCycle(){
        ParkingLotRequestDto lotRequest = ParkingLotRequestDto.builder()
                .name("Test Lot")
                .location("Test Location")
                .basePricePerHour(10.0)
                .totalSlots(10)
                .build();

        ParkingLotDto currentLot =  parkingLotService.createParkingLot(lotRequest);
        Optional<ParkingLot> lot = parkingLotRepository.findById(currentLot.getId());
        Long parkingLotId = currentLot.getId();

        // Lot availability
        assertNotNull(parkingLotService.getAllParkingLots());
        assertEquals(lot, parkingLotService.getParkingLotById(parkingLotId));


        // Entry
        EntryRequestDto entryRequest = EntryRequestDto.builder()
                .vehicleNumber("MH04AB1234")
                .vehicleType(VehicleType.CAR)
                .parkingLotId(parkingLotId)
                .build();

        ParkingTicketDto ticket = parkingOperationService.enterVehicle(entryRequest);

        assertEquals(9, parkingSlotRepository.countByParkingLotIdAndSlotStatus(parkingLotId, SlotStatus.AVAILABLE));

        assertNotNull(ticket.getSessionId());

        Optional<ParkingSession> currentSession = parkingSessionRepository.findByVehicleIdAndSessionStatus(
                vehicleRepository.findByVehicleNumber("MH04ab1234").get().getId(),
                SessionStatus.ACTIVE);

        assertTrue(currentSession.isPresent());


        // Exit
        ExitRequestDto exitRequest = ExitRequestDto.builder().vehicleNumber("MH04AB1234").build();

        BillDto bill = parkingOperationService.exitVehicle(exitRequest);

        assertNotNull(bill.getTotalAmount());

        assertEquals("MH04AB1234", bill.getVehicleNumber());

        Optional<ParkingSession> exitSession = parkingSessionRepository.findById(bill.getSessionId());
        assertEquals(SessionStatus.COMPLETED, exitSession.get().getSessionStatus());

        Optional<ParkingSlot> exitSlot = parkingSlotRepository.findById(exitSession.get().getParkingSlot().getId());
        assertEquals(SlotStatus.AVAILABLE, exitSlot.get().getSlotStatus());

        // Billing
        assertEquals(0.0, bill.getTotalAmount());
    }

    @Test
    void terminationTesting(){

        ParkingLotRequestDto lotRequest = ParkingLotRequestDto.builder()
                .name("Test Lot")
                .location("Test Location")
                .basePricePerHour(10.0)
                .totalSlots(10)
                .build();

        ParkingLotDto currentLot =  parkingLotService.createParkingLot(lotRequest);
        Long parkingLotId = currentLot.getId();

        EntryRequestDto entryRequest = EntryRequestDto.builder()
                .vehicleNumber("MH04AB1234")
                .vehicleType(VehicleType.CAR)
                .parkingLotId(parkingLotId)
                .build();

        ParkingTicketDto ticket = parkingOperationService.enterVehicle(entryRequest);

        parkingSessionService.terminateSession(ticket.getSessionId());

        Optional<ParkingSession> currSession = parkingSessionRepository.findById(ticket.getSessionId());
        assertEquals(SessionStatus.TERMINATED, currSession.get().getSessionStatus());
    }
}
