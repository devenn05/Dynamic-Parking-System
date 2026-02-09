import { Component, OnInit, signal, ViewChild, AfterViewInit, Inject, PLATFORM_ID, effect } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ParkingService } from '../../services/parking';
import { ParkingSession, ParkingLot } from '../../models/models.interface';

// Material Imports
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-parking-sessions',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    MatTableModule, 
    MatPaginatorModule, 
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule
  ],
  templateUrl: './parking-sessions.html',
  styleUrl: '../../app.css',
})
export class ParkingSessions implements OnInit {
  // Data Sources for the tables
  activeDataSource = new MatTableDataSource<ParkingSession>([]);
  historyDataSource = new MatTableDataSource<ParkingSession>([]);

  // Columns for the tables
  activeColumns: string[] = ['vehicleNumber', 'vehicleType', 'parkingLotName', 'slotNumber', 'entryTime', 'actions'];
  historyColumns: string[] = ['vehicleNumber', 'vehicleType', 'parkingLotName', 'slotNumber', 'entryTime', 'exitTime', 'totalAmount', 'status'];

  // Paginators
  @ViewChild('activePaginator') activePaginator!: MatPaginator;
  @ViewChild('historyPaginator') historyPaginator!: MatPaginator;

  // Search State Signals
  lots = signal<ParkingLot[]>([]);
  selectedLotId = signal<number | null>(null);
  searchTerm = signal<string>('');
  searchDate = signal<string>('');

  constructor(private parkingService: ParkingService, @Inject(PLATFORM_ID) private platformId: Object) {
    // Whenever filters change, update the tables
    effect(() => {
        this.applyFilters();
    });
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.parkingService.getAllLots().subscribe(data => {
        this.lots.set(data);
        this.loadData();
      });
    }
  }

  loadData() {
    this.parkingService.getActiveSessions(this.selectedLotId() || undefined)
      .subscribe(data => {
          this.activeDataSource.data = data;
          this.activeDataSource.paginator = this.activePaginator;
      });

    this.parkingService.getAllSessions(this.selectedLotId() || undefined)
      .subscribe(data => {
          this.historyDataSource.data = data;
          this.historyDataSource.paginator = this.historyPaginator;
      });
  }

  applyFilters() {
    const term = this.searchTerm().toLowerCase();
    const date = this.searchDate();

    const filterPredicate = (s: ParkingSession) => {
        const matchesVehicle = s.vehicleNumber.toLowerCase().includes(term);
        const matchesDate = !date || s.entryTime.startsWith(date);
        return matchesVehicle && matchesDate;
    };

    // Note: Since the backend doesn't filter by text/date, we do it frontend side
    // on the already fetched arrays to keep things snappy.
    this.activeDataSource.filterPredicate = filterPredicate;
    this.activeDataSource.filter = 'trigger'; // dummy value to trigger filter

    this.historyDataSource.filterPredicate = filterPredicate;
    this.historyDataSource.filter = 'trigger';
  }

  onTerminate(session: any) {
    const confirmMsg = `MANUAL OVERRIDE:\nForce terminate vehicle: ${session.vehicleNumber}?`;
    if (!confirm(confirmMsg)) return;

    this.parkingService.terminateSession(session.sessionId).subscribe({
      next: () => {
        alert("Session Terminated Successfully.");
        this.loadData();
      },
      error: (err) => alert("Failed to terminate")
    });
  }

  resetFilters() {
      this.searchTerm.set('');
      this.searchDate.set('');
      this.selectedLotId.set(null);
      this.loadData();
  }
}