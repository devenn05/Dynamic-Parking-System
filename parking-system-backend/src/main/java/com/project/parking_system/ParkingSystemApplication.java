package com.project.parking_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry point for the Parking System Application.
 * This application implements a Dynamic Pricing System for Parking Lots.
 * It manages parking lots, slots, vehicle entry/exit, and calculates dynamic charges
 * based on duration and occupancy rules.
 */

@SpringBootApplication
public class ParkingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingSystemApplication.class, args);
	}

}
