package com.project.parking_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a Parking Facility.
 * Maps to the 'parking_lots' table in the database.
 * Serves as the parent entity for Parking Slots.
 */

@Entity
@Table(name = "parking_lots")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false, name = "total_slots")
    private Integer totalSlots;

    @Column(nullable = false, name = "base_price_per_hour")
    private Double basePricePerHour;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    //JPA Lifecycle Hook.
    //Automatically sets the 'createdAt' timestamp before the entity is persisted to the database for the first time.
    @PrePersist
    protected void onCreate(){this.createdAt = LocalDateTime.now();}
}
