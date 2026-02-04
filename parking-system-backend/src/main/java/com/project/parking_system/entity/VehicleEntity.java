package com.project.parking_system.entity;

import com.project.parking_system.enums.VehicleTypeEnum;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a Vehicle.
 * Maps to the 'vehicles' table.
 * Acts as a registry of known vehicles. A vehicle is created once
 * and reused across multiple Parking Sessions.
 */

@Entity
@Table(name = "vehicles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_number", nullable = false, unique = true)
    private String vehicleNumber;

    // Enum -> Type of vehicle (CAR, BIKE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleTypeEnum vehicleTypeEnum;

}
