package com.project.parking_system.service;

import com.project.parking_system.entity.Vehicle;
import com.project.parking_system.enums.VehicleType;

import java.util.Optional;

/**
 * Service interface for Vehicle Registry.
 */

public interface VehicleService {

    // If vehicle exists, return it; otherwise create it.
    Vehicle findOrCreateVehicle(String vehicleNumber, VehicleType vehicleType);

    //  Finds a vehicle through Vehicle Number.
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
}
