package com.project.parking_system.service.impl;

import com.project.parking_system.entity.Vehicle;
import com.project.parking_system.enums.VehicleType;
import com.project.parking_system.exception.BusinessException;
import com.project.parking_system.repository.VehicleRepository;
import com.project.parking_system.service.VehicleService;
import com.project.parking_system.utils.ParkingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service Implementation for Vehicle Management.
 */

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    // Registers a vehicle if it's new. Returns existing one if found.
    // Includes a check to ensure the vehicle type hasn't changed (e.g., Bike trying to enter as Car).
    @Override
    public Vehicle findOrCreateVehicle(String vehicleNumber, VehicleType vehicleType){

        // 1. Sanitize the input
        String cleanNumber = ParkingUtils.normalizeVehicleNumber(vehicleNumber);

        // 1. Check if vehicle exists
        Optional<Vehicle> existingVehicle = vehicleRepository.findByVehicleNumber(cleanNumber);

        // 2. If yes, return it. If no, create and save a new one.
        if (existingVehicle.isPresent()){
            Vehicle vehicle = existingVehicle.get();

            if (vehicle.getVehicleType() != vehicleType){
                throw new BusinessException("Vehicle " + vehicleNumber + " is registered as " + vehicle.getVehicleType() + ". Cannot process as " + vehicleType);
            }
            return vehicle;
        }

        Vehicle newVehicle = Vehicle.builder()
                .vehicleNumber(cleanNumber)
                .vehicleType(vehicleType)
                .build();

        return vehicleRepository.save(newVehicle);
    }

    @Override
    public Optional<Vehicle> findByVehicleNumber(String vehicleNumber){
        return vehicleRepository.findByVehicleNumber(ParkingUtils.normalizeVehicleNumber(vehicleNumber));
    }
}