package com.project.parking_system.service.impl;

import com.project.parking_system.entity.VehicleEntity;
import com.project.parking_system.enums.VehicleTypeEnum;
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
    public VehicleEntity findOrCreateVehicle(String vehicleNumber, VehicleTypeEnum vehicleTypeEnum){

        // 1. Sanitize the input
        String cleanNumber = ParkingUtils.normalizeVehicleNumber(vehicleNumber);

        // 1. Check if vehicle exists
        Optional<VehicleEntity> existingVehicle = vehicleRepository.findByVehicleNumber(cleanNumber);

        // 2. If yes, return it. If no, create and save a new one.
        if (existingVehicle.isPresent()){
            VehicleEntity vehicleEntity = existingVehicle.get();

            if (vehicleEntity.getVehicleTypeEnum() != vehicleTypeEnum){
                throw new BusinessException("Vehicle " + vehicleNumber + " is registered as " + vehicleEntity.getVehicleTypeEnum() + ". Cannot process as " + vehicleTypeEnum);
            }
            return vehicleEntity;
        }

        VehicleEntity newVehicleEntity = VehicleEntity.builder()
                .vehicleNumber(cleanNumber)
                .vehicleTypeEnum(vehicleTypeEnum)
                .build();

        return vehicleRepository.save(newVehicleEntity);
    }

    @Override
    public Optional<VehicleEntity> findByVehicleNumber(String vehicleNumber){
        return vehicleRepository.findByVehicleNumber(ParkingUtils.normalizeVehicleNumber(vehicleNumber));
    }
}