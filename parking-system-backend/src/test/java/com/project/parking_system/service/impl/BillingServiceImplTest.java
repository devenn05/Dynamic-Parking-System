package com.project.parking_system.service.impl;

import com.project.parking_system.dto.BillingResultDto;
import com.project.parking_system.repository.ParkingSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillingServiceImplTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @InjectMocks
    private BillingServiceImpl billingService;


    // Test Case 1 - Standard Parking
    @Test
    void shouldCalculateStandardBill(){
        LocalDateTime entry = LocalDateTime.now().minusHours(2);
        LocalDateTime exit = LocalDateTime.now();

        Double basePricePerHour = 20.0;
        Long parkingLotId = 2L;
        Integer totalSlots = 10;

        when(parkingSlotRepository.countByParkingLotIdAndSlotStatus(any(), any()))
                .thenReturn(0L);

        BillingResultDto result = billingService.calculateBill(entry, exit, basePricePerHour, parkingLotId, totalSlots);

        assertEquals(40.0, result.getTotalAmount());
    }

    // Test Case 2 - Standard Parking Free Parking
    @Test
    void freeParkingBill(){
        LocalDateTime entry = LocalDateTime.now().minusMinutes(20);
        LocalDateTime exit = LocalDateTime.now();

        Double basePricePerHour = 50.0;
        Long parkingLotId = 2L;
        Integer totalSlots = 10;

        when(parkingSlotRepository.countByParkingLotIdAndSlotStatus(any(), any()))
                .thenReturn(0L);

        BillingResultDto result = billingService.calculateBill(entry, exit, basePricePerHour, parkingLotId, totalSlots);

        assertEquals(0.0, result.getTotalAmount());
    }
    // Test Case 3 - Surge Pricing and 30 minutes Free Calculation
    @Test
    void surgePricingBill(){
        LocalDateTime entry = LocalDateTime.now().minusHours(3).minusMinutes(37);
        // i.e. 3hr 27 mins, removing 30mins of free 3hr 7mins ceiling to 4

        LocalDateTime exit = LocalDateTime.now();

        Double basePricePerHour = 100.0;
        Long parkingLotId = 2L;
        Integer totalSlots = 10;

        when(parkingSlotRepository.countByParkingLotIdAndSlotStatus(any(), any()))
                .thenReturn(9L);

        BillingResultDto result = billingService.calculateBill(entry, exit, basePricePerHour, parkingLotId, totalSlots);

        assertEquals(1.5, result.getAppliedMultiplier());
        assertEquals(600, result.getTotalAmount());  // 4 * 100 * 1.5
    }
}
