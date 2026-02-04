package com.project.parking_system.repository;

import com.project.parking_system.entity.ParkingSlotEntity;
import com.project.parking_system.enums.SlotStatusEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing ParkingSlot entities.
 * Critical for handling slot allocation concurrency.
 */

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlotEntity, Long> {

    /**
     * What it does: Finds available slots for a lot, ordered by slot number (Logic: Fill Slot 1, then 2, etc.).
     * SQL: SELECT * FROM parking_slots WHERE parking_lot_id = ? AND status = ? ORDER BY slot_number ASC
     * Concurrency Note: Uses PESSIMISTIC_WRITE lock.
     * When a transaction calls this to find a slot, it locks the rows. This prevents two cars from being assigned the same "Slot 1" at the exact same millisecond.
    */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ParkingSlotEntity> findByParkingLotIdAndSlotStatusOrderBySlotNumberAsc(Long parkingLotId, SlotStatusEnum status);

    // Finds Parking Slot by parking lot Id ordering them Slot numbers in Ascending.
    List<ParkingSlotEntity> findByParkingLotIdOrderBySlotNumberAsc(Long parkingLotId);

    // SQL: SELECT COUNT(*) FROM parking_slots WHERE parking_lot_id = ? AND status = ?
    long countByParkingLotIdAndSlotStatus(Long parkingLotId, SlotStatusEnum status);
}
