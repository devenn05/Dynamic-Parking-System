package com.project.parking_system.service;

import com.project.parking_system.entity.VehicleEntity;
import com.project.parking_system.enums.VehicleTypeEnum;

import java.util.Optional;

/**
 * Service interface for Vehicle Registry.
 */

public interface VehicleService {

    // If vehicle exists, return it; otherwise create it.
    VehicleEntity findOrCreateVehicle(String vehicleNumber, VehicleTypeEnum vehicleTypeEnum);

    //  Finds a vehicle through Vehicle Number.
    Optional<VehicleEntity> findByVehicleNumber(String vehicleNumber);
}
