package com.project.parking_system.mapper;


import com.project.parking_system.dto.ParkingLotDto;
import com.project.parking_system.dto.ParkingLotRequestDto;
import com.project.parking_system.entity.ParkingLot;
import org.springframework.stereotype.Component;

/**
 * Utility class for object mapping.
 * Decouples the Internal Database Entities from the External API DTOs.
 * Ensures that changes in the database structure do not strictly require breaking changes
 * in the API contract, and vice versa.
 */

@Component
public class ParkingLotMapper {

    /**
     * Converts a ParkingLot entity (from the database) into a ParkingLotDTO (to send to the user).
     */
    public static ParkingLotDto toDto(ParkingLot parkingLot){
        return ParkingLotDto.builder()
                .id(parkingLot.getId())
                .name(parkingLot.getName())
                .location(parkingLot.getLocation())
                .totalSlots(parkingLot.getTotalSlots())
                .basePricePerHour(parkingLot.getBasePricePerHour())
                .createdAt(parkingLot.getCreatedAt()).build();
    }

    /**
     * Converts a ParkingLotRequest (from the user) into a ParkingLot entity (to be saved in the database).
     */
    public static ParkingLot toEntity(ParkingLotRequestDto parkingLotRequestDTO){
        return ParkingLot.builder()
                .name(parkingLotRequestDTO.getName())
                .location(parkingLotRequestDTO.getLocation())
                .totalSlots(parkingLotRequestDTO.getTotalSlots())
                .basePricePerHour(parkingLotRequestDTO.getBasePricePerHour()).build();
    }
}
