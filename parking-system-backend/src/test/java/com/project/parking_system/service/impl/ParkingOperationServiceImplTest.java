package com.project.parking_system.service.impl;

import com.project.parking_system.dto.*;
import com.project.parking_system.entity.ParkingLot;
import com.project.parking_system.entity.ParkingSession;
import com.project.parking_system.entity.ParkingSlot;
import com.project.parking_system.entity.Vehicle;
import com.project.parking_system.enums.VehicleType;
import com.project.parking_system.exception.BusinessException;
import com.project.parking_system.exception.ResourceNotFoundException;
import com.project.parking_system.repository.ParkingLotRepository;
import com.project.parking_system.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingOperationServiceImplTest {

    @Mock private VehicleService vehicleService;
    @Mock private ParkingSlotService parkingSlotService;
    @Mock private ParkingLotService parkingLotService;
    @Mock private ParkingSessionService parkingSessionService;
    @Mock private ParkingLotRepository parkingLotRepository;
    @Mock private BillingService billingService;

    @InjectMocks
    private ParkingOperationServiceImpl parkingOperationService;

    private EntryRequestDto entryRequest;
    private ExitRequestDto exitRequest;
    private ParkingLot mockLot;
    private Vehicle mockVehicle;
    private ParkingSlot mockSlot;
    private ParkingSession mockSession;
    private BillingResultDto billingResult;

    // Runs before every Tests
    @BeforeEach
    void setUp(){

        // Entry Request
        entryRequest = new EntryRequestDto();
        entryRequest.setVehicleNumber("MH12AB1234");
        entryRequest.setVehicleType(VehicleType.CAR);
        entryRequest.setParkingLotId(1L);

        // Exit Request
        exitRequest = new ExitRequestDto();
        exitRequest.setVehicleNumber("MH12AB1234");

        billingResult = BillingResultDto.builder()
                .totalAmount(100.0)
                .appliedMultiplier(1.25)
                .billableHours(2L)
                .build();

        mockLot = ParkingLot.builder().id(1L).name("Mock Lot").totalSlots(100).basePricePerHour(50.0).build();
        mockVehicle = Vehicle.builder().vehicleNumber("MH12AB1234").vehicleType(VehicleType.CAR).build();
        mockSlot = ParkingSlot.builder().id(25L).slotNumber(1).parkingLot(mockLot).build();
        mockSession = ParkingSession.builder()
                .id(100L)
                .vehicle(mockVehicle)
                .parkingSlot(mockSlot)
                .entryTime(LocalDateTime.now())
                .build();
    }

    //  Test Case 1 - Successful Entry
    // Checks if the Entry logic is holding correctly.
    @Test
    void shouldAllowVehicleEntry() {

        // 1. When the service asks the repository if lot #1 exists, say yes and return our mockLot.
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(mockLot));

        // 2. When the service tries to find or create the vehicle, return our mockVehicle.
        when(vehicleService.findOrCreateVehicle(any(String.class), any(VehicleType.class))).thenReturn(mockVehicle);

        // 3. When the service checks for an active session, say no (return empty).
        when(parkingSessionService.findActiveSession(mockVehicle)).thenReturn(Optional.empty());

        // 4. When the service asks for an available slot, return our mockSlot.
        when(parkingSlotService.findFirstAvailableSlot(1L)).thenReturn(mockSlot);

        // 5. When the service tries to create a new session, return our mockSession.
        when(parkingSessionService.createSession(mockVehicle, mockSlot)).thenReturn(mockSession);

        // Now, we execute the actual method we want to test.
        ParkingTicketDto dto = parkingOperationService.enterVehicle(entryRequest);

        assertNotNull(dto);
        assertEquals(100L,dto.getSessionId());
        assertEquals("MH12AB1234", dto.getVehicleNumber());
        assertEquals("Mock Lot", dto.getParkingLotName());
    }

    @Test
    void exceptionEntryVehicle(){
        entryRequest.setVehicleType(VehicleType.BIKE);

        // 1. The check for the parking lot must succeed for the code to proceed.
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(mockLot));

        // 2. We program the vehicle service to be the source of the error.
        // When it's called, it will throw the exception we want to test.
        when(vehicleService.findOrCreateVehicle("MH12AB1234", VehicleType.BIKE))
                .thenThrow(new BusinessException("Vehicle is already registered as CAR. Cannot process as BIKE"));

        // This captures the exception...
        assertThrows(BusinessException.class, () -> parkingOperationService.enterVehicle(entryRequest));
    }

    @Test
    void shouldAllowExitVehicle(){

        // 1. When the service asks for the vehicle, return our mockVehicle.
        when(vehicleService.findByVehicleNumber("MH12AB1234")).thenReturn(Optional.of(mockVehicle));

        // 2. When the service asks for an active session, return our mockSession.
        when(parkingSessionService.findActiveSession(mockVehicle)).thenReturn(Optional.of(mockSession));

        // 3. When the service calls the billing logic, return our predefined billingResult.
        when(billingService.calculateBill(any(LocalDateTime.class), any(LocalDateTime.class), anyDouble(), anyLong(), anyInt())).thenReturn(billingResult);

        // Now, we execute the actual method we want to test.
        BillDto dto = parkingOperationService.exitVehicle(exitRequest);

        assertNotNull(dto);
        assertEquals(100.0, dto.getTotalAmount());
        assertEquals("MH12AB1234", dto.getVehicleNumber());

        // Verifies that after the exit vehicle the slot that was in use is marked as Available.
        verify(parkingSlotService, times(1)).markSlotAsAvailable(mockSlot.getId());
    }

    @Test
    void exceptionExitVehicle(){

        // 1. The vehicle lookup succeeds.
        when(vehicleService.findByVehicleNumber("MH12AB1234")).thenReturn(Optional.of(mockVehicle));

        // 2. But when checking for an active session, we return empty to trigger the error.
        when(parkingSessionService.findActiveSession(mockVehicle)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> parkingOperationService.exitVehicle(exitRequest));
    }
}
