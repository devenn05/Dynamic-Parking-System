import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ParkingService } from '../../services/parking';
import { FormsModule } from '@angular/forms';
import { ParkingSession, ParkingLot } from '../../models/models.interface';
import { PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

/**
 * Parking Sessions Component
 * -------------------------------------------------------------------------
 * Displays lists of vehicles in the system.
 * Features:
 * 1. "Active" Tab: Shows vehicles currently inside the parking lots.
 * 2. "History" Tab: Shows a complete log of all past and present sessions.
 * 3. Lot Filter: Allows narrowing down data by a specific Parking Lot.
 */

@Component({
  selector: 'app-parking-sessions',
  imports: [CommonModule, FormsModule],
  templateUrl: './parking-sessions.html',
  styleUrl: '../../app.css',
})
export class ParkingSessions implements OnInit {
  
  // Data Signals for the tables
  activeSession = signal<ParkingSession[]>([])
  historySession = signal<ParkingSession[]>([])

  // UI State: Toggle between 'ACTIVE' view and 'HISTORY' view
  view = signal<'ACTIVE' | 'HISTORY'>('ACTIVE');
  
  switchToActive() {
    this.view.set('ACTIVE');
  }

  switchToHistory() {
    this.view.set('HISTORY');
  }

  // Data for the Filter Dropdown
  lots = signal<ParkingLot[]>([])

  // Selected Lot ID for filtering (null = Show All)
  selectedLotId = signal<number | null>(null)

  constructor(private parkingService: ParkingService, @Inject(PLATFORM_ID) private platformId: Object){}

  ngOnInit(): void {
     if (isPlatformBrowser(this.platformId)){
          // 1. Fetch Lots first to populate the dropdown
    this.parkingService.getAllLots().subscribe(data => {
      this.lots.set(data);
      this.loadData();
    });
     }
  }

  loadData(){
    // Fetch Active Sessions 
    this.parkingService.getActiveSessions(this.selectedLotId() || undefined)
        .subscribe(data => this.activeSession.set(data));

     // Fetch History
    this.parkingService.getAllSessions(this.selectedLotId() || undefined)
        .subscribe(data => this.historySession.set(data));
  }

   // 1. Search State Variables
  searchTerm = signal<string>(''); // For Vehicle Number
  searchDate = signal<string>(''); // For Date (YYYY-MM-DD)

  // 2. Filter Logic Helpers
  // We use standard "Getters". In Angular, using a getter in the template
  // automatically updates the view when the data changes.

  get filteredActiveSessions() {
    return this.activeSession().filter(s => this.matchesFilters(s));
  }

  get filteredHistorySessions() {
    return this.historySession().filter(s => this.matchesFilters(s));
  }

  // Core Matching Logic
  private matchesFilters(session: any): boolean {
    // 1. Check Vehicle Number (Case insensitive)
    const matchesVehicle = session.vehicleNumber.toLowerCase()
                           .includes(this.searchTerm().toLowerCase());
    
    // 2. Check Date (Compare YYYY-MM-DD strings)
    // entryTime is ISO (e.g., "2024-02-01T10:00:00")
    // searchDate is YYYY-MM-DD
    const matchesDate = !this.searchDate() || session.entryTime.startsWith(this.searchDate());

    return matchesVehicle && matchesDate;
  }

  onTerminate(session: any){

    // Confirmation message to Terminate the slot.
    const confirmMsg = `MANUAL OVERRIDE:\nAre you sure you want to forcibly terminate the session for vehicle: ${session.vehicleNumber}?\n\nThis will mark the slot as free and set the bill to 0.`;
    if(!confirm(confirmMsg)) return;

    this.parkingService.terminateSession(session.sessionId).subscribe({
      next: () => {
        alert("Session Terminated Successfully.");
        this.loadData(); 
      },
      error: (err) => alert("Failed to terminate: " + (err.error?.message || "Unknown error"))
    })
  }

}