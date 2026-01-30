import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ParkingService } from '../../services/parking';
import { FormsModule } from '@angular/forms';
import { ParkingSession, ParkingLot } from '../../models/models.interface';

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

  constructor(private parkingService: ParkingService){}

  ngOnInit(): void {
    // 1. Fetch Lots first to populate the dropdown
    this.parkingService.getAllLots().subscribe(data => {
      this.lots.set(data);
      this.loadData();
    });
  }

  loadData(){
    // Fetch Active Sessions 
    this.parkingService.getActiveSessions(this.selectedLotId() || undefined)
        .subscribe(data => this.activeSession.set(data));

     // Fetch History
    this.parkingService.getAllSessions(this.selectedLotId() || undefined)
        .subscribe(data => this.historySession.set(data));
  }
}