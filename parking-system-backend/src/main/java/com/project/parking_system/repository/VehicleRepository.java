package com.project.parking_system.repository;

import com.project.parking_system.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing Vehicle entities.
 * Acts as the registry for unique vehicles entering the system.
 */

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    // SQL: SELECT * FROM vehicles WHERE vehicle_number = ?
    Optional<VehicleEntity> findByVehicleNumber(String vehicleNumber);
}
