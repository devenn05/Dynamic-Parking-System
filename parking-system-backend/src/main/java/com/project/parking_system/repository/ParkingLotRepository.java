package com.project.parking_system.repository;

import com.project.parking_system.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing ParkingLot entities.
 * Provides standard CRUD operations (Create, Read, Update, Delete) via Spring Data JPA.
 */

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

}
