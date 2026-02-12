// -------------------------------------------------------------------------
// DOMAIN MODELS / DTOS
// These interfaces define the shape of data exchanged with the Spring Boot API.
// They must strictly match the JSON structure returned by the Java DTOs.
// -------------------------------------------------------------------------


// Matches: ParkingLotDTO
export interface ParkingLot{
    id: number;
    name: string;
    location: string;
    totalSlots: number;
    availableSlots: number;
    basePricePerHour: number;
    createdAt: string;
}

//  Matches: ParkingSlotDTO
export interface ParkingSlot{
    id: number;
    slotNumber: number;
    slotStatus: 'AVAILABLE' | 'OCCUPIED';
}

//Matches: EntryRequest
// Sent when a vehicle attempts to enter.
export interface EntryRequest{
    vehicleNumber: string;
    vehicleType: 'CAR' | 'BIKE';
    parkingLotId: number;
}

// Matches: ExitRequest
// Sent when a vehicle attempts to exit.
export interface ExitRequest{
    vehicleNumber: string;
    parkingLotId: number;
}

// Response received after a successful entry.
// Matches: ParkingTicketDTO
export interface ParkingTicket{
    sessionId: number;
    vehicleNumber: string;
    slotNumber: number;
    parkingLotName: string;
    entryTime: string;
}

// Response received after a successful exit.
// Matches: BillDTO
export interface Bill{
    sessionId: number;
    vehicleNumber: string;
    entryTime: string;
    exitTime: string;
    duration: number;
    totalAmount: number;
    parkingLotName: string;
    basePricePerHour: number;
    occupancyMultiplier: number;
    billableHours: number;
}

// Matches: ParkingSessionDTO (Java)
export interface ParkingSession{
    sessionId: number;
    vehicleNumber: string;
    vehicleType: string;
    parkingLotName?: string;
    slotNumber: number;
    entryTime: string;
    exitTime?: string;
    totalAmount?: number;
    status: 'ACTIVE' | 'COMPLETED' | 'TERMINATED';
}