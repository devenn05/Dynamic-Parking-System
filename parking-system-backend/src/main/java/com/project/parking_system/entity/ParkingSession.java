package com.project.parking_system.entity;

import com.project.parking_system.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an active or completed parking visit for a single vehicle.
 * This is the core transactional entity that links a Vehicle to a ParkingSlot for a specific duration.
 * It tracks the entire lifecycle of a parking event from entry to exit.
 *
 * @see Vehicle
 * @see ParkingSlot
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"vehicle", "parkingSlot"})
@Table(name = "parking_sessions")
@Builder
public class ParkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FetchType.LAZY suggests we don't load vehicle and parking_slot details unless requested.
     */

    // OPTIMISTIC LOCKING:
    // This field prevents lost updates.
    // If two threads try to complete the same session, one will fail with an OptimisticLockException.
    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_slot_id", nullable = false)
    private ParkingSlot parkingSlot;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus sessionStatus;

}
