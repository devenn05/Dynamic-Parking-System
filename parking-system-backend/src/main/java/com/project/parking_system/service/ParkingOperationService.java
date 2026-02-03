package com.project.parking_system.service;

import com.project.parking_system.dto.BillDTO;
import com.project.parking_system.dto.EntryRequest;
import com.project.parking_system.dto.ExitRequest;
import com.project.parking_system.dto.ParkingTicketDTO;

/**
 * Service interface defining the High-Level flow of the application.
 * This service orchestrates the interaction between Vehicles, Slots, and Sessions.
 */

public interface ParkingOperationService {

    // Plans the entry of the Vehicle
    ParkingTicketDTO enterVehicle(EntryRequest request);

    // Plans the exit of the Vehicle
    BillDTO exitVehicle(ExitRequest request);

}
