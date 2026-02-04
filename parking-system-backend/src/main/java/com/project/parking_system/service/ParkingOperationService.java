package com.project.parking_system.service;

import com.project.parking_system.dto.BillDto;
import com.project.parking_system.dto.EntryRequestDto;
import com.project.parking_system.dto.ExitRequestDto;
import com.project.parking_system.dto.ParkingTicketDto;

/**
 * Service interface defining the High-Level flow of the application.
 * This service orchestrates the interaction between Vehicles, Slots, and Sessions.
 */

public interface ParkingOperationService {

    // Plans the entry of the Vehicle
    ParkingTicketDto enterVehicle(EntryRequestDto request);

    // Plans the exit of the Vehicle
    BillDto exitVehicle(ExitRequestDto request);

}
