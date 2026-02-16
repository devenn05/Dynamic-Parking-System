import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ParkingLot, ParkingSlot,EntryRequest,ExitRequest,ParkingTicket, Bill, ParkingSession } from '../models/models.interface';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

/**
 * Parking Service
 * -------------------------------------------------------------------------
 * Acts as the bridge between the Angular Frontend and Spring Boot Backend.
 * Contains methods for all HTTP API calls defined in the requirements.
 */

@Injectable({
  providedIn: 'root',
})
export class ParkingService {
  
  // Base URL pointing to the local Spring Boot Server
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient){}

  // -----------------------------------------------------------------------
  // PARKING LOT MANAGEMENT API
  // -----------------------------------------------------------------------


  // GET /api/parking-lots
  // Retrieves all parking lots with current availability stats.
  getAllLots(): Observable<ParkingLot[]> {
    return this.http.get<ParkingLot[]>(`${this.baseUrl}/parking-lots`);
  }

  //GET /api/parking-lot/{id}
  // Get Parking Lot by id
  getParkingLotById(lotId: number): Observable<ParkingLot>{
    return this.http.get<ParkingLot>(`${this.baseUrl}/parking-lots/${lotId}`)
  }

  //POST /api/parking-lots
  //Creates a new parking lot configuration.
  createLot(lot: any): Observable<ParkingLot>{
    return this.http.post<ParkingLot>(`${this.baseUrl}/parking-lots`, lot)
  }

  // GET /api/parking-lots/{id}/slots
  // Fetches the grid of slots (Available/Occupied) for a specific lot.
  getSlots(lotId: number): Observable<ParkingSlot[]>{
    return this.http.get<ParkingSlot[]>(`${this.baseUrl}/parking-lots/${lotId}/slots`)
  }

  // PUT /api/parking-lots/{id}
  // Update Parking Lot
  updateLot(id: number, lotData: any): Observable<ParkingLot> {
    return this.http.put<ParkingLot>(`${this.baseUrl}/parking-lots/${id}`, lotData);
  }

  // DELETE /api/parking-lot/{id}
  // Delete ParkingLot
  deleteLot(id: number): Observable<void>{
    return this.http.delete<void>(`${this.baseUrl}/parking-lots/${id}`)
  }

  // POST /api/parking/entry
  // Triggers the entry logic (Vehicle Validation -> Slot Assignment -> Ticket Issue).
  entryVehicle(request: EntryRequest): Observable<ParkingTicket>{
    return this.http.post<ParkingTicket>(`${this.baseUrl}/parking/entry`, request)
  }

  // POST /api/parking/exit
  // Triggers the exit logic (Validation -> Bill Calculation -> Payment).
  exitVehicle(request: ExitRequest): Observable<Bill>{
    return this.http.post<Bill>(`${this.baseUrl}/parking/exit`, request)
  }

  // GET /api/sessions/active
  // Fetches only currently parked vehicles.
  getActiveSessions(lotId?: number): Observable<ParkingSession[]>{
    let url = `${this.baseUrl}/sessions/active`;
    if (lotId) url += `?lotId=${lotId}`;
    return this.http.get<ParkingSession[]>(url);
  }

  // GET /api/sessions/history
  // Fetches all records (Active + Completed).
  getAllSessions(lotId?: number): Observable<ParkingSession[]>{
    let url = `${this.baseUrl}/sessions/history`;
    if (lotId) url += `?lotId=${lotId}`;
    return this.http.get<ParkingSession[]>(url);
  }

  // POST /api/sessions/{id}/terminate
  // Terminates the Session associated with that id
  terminateSession(sessionId: number): Observable<void>{
    return this.http.post<void>(`${this.baseUrl}/sessions/${sessionId}/terminate`, {});
  }

}
