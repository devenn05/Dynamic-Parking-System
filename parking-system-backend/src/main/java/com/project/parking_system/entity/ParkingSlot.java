package com.project.parking_system.entity;

import com.project.parking_system.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a physical parking space.
 * Maps to 'parking_slots' table.
 * Each slot belongs to a specific ParkingLot and tracks its own availability.
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"parkingLot"})
@Table(name = "parking_slots")
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number", nullable = false)
    private Integer slotNumber;

    // Enum -> Tracks if the slot is AVAILABLE or OCCUPIED.
    // Critical for finding free space during vehicle entry.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus slotStatus;

    /**
     * FetchType.LAZY suggests we don't load parking_lot details unless requested.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;


}
