package com.project.parking_system.mapper;


import com.project.parking_system.dto.ParkingLotDto;
import com.project.parking_system.dto.ParkingLotRequestDto;
import com.project.parking_system.entity.ParkingLotEntity;
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
    public static ParkingLotDto toDto(ParkingLotEntity parkingLotEntity){
        return ParkingLotDto.builder()
                .id(parkingLotEntity.getId())
                .name(parkingLotEntity.getName())
                .location(parkingLotEntity.getLocation())
                .totalSlots(parkingLotEntity.getTotalSlots())
                .basePricePerHour(parkingLotEntity.getBasePricePerHour())
                .createdAt(parkingLotEntity.getCreatedAt()).build();
    }

    /**
     * Converts a ParkingLotRequest (from the user) into a ParkingLot entity (to be saved in the database).
     */
    public static ParkingLotEntity toEntity(ParkingLotRequestDto parkingLotRequestDTO){
        return ParkingLotEntity.builder()
                .name(parkingLotRequestDTO.getName())
                .location(parkingLotRequestDTO.getLocation())
                .totalSlots(parkingLotRequestDTO.getTotalSlots())
                .basePricePerHour(parkingLotRequestDTO.getBasePricePerHour()).build();
    }
}
