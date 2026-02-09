import { Component, OnInit, signal, ViewChild, AfterViewInit, Inject, PLATFORM_ID, effect } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ParkingService } from '../../services/parking';
import { ParkingSession, ParkingLot } from '../../models/models.interface';
import { ConfirmDialog } from '../shared/confirm-dialog';

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
import {MatSort, Sort, MatSortModule} from '@angular/material/sort';
import { NotificationService } from '../../services/notification';
import { MatSnackBarModule } from '@angular/material/snack-bar'; 
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

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
    MatCardModule,
    MatSortModule,
    MatSnackBarModule,
    MatDialogModule
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
  @ViewChild('activeSort') activeSort!: MatSort;
  @ViewChild('historySort') historySort!: MatSort;

  // Search State Signals
  lots = signal<ParkingLot[]>([]);
  selectedLotId = signal<number | null>(null);
  searchTerm = signal<string>('');

  constructor(
    private parkingService: ParkingService,
    private notificationService: NotificationService, 
    private dialog: MatDialog,
    @Inject(PLATFORM_ID) private platformId: Object) {
    // Whenever filters change, update the tables
    effect(() => {
        this.applyFilters();
    });
  }

  ngOnInit(): void {
    this.parkingService.getAllLots().subscribe({
        next: (data) => {
           this.lots.set(data);
           this.loadData();
        },
        error: (err) => this.notificationService.showError("Failed to connect to server")
      });
  }

  loadData() {
    this.parkingService.getActiveSessions(this.selectedLotId() || undefined)
      .subscribe(data => {
          this.activeDataSource.data = data;
          this.activeDataSource.paginator = this.activePaginator;
          this.activeDataSource.sort = this.activeSort;
      });

    this.parkingService.getAllSessions(this.selectedLotId() || undefined)
      .subscribe(data => {
          this.historyDataSource.data = data;
          this.historyDataSource.paginator = this.historyPaginator;
          this.historyDataSource.sort = this.historySort;
      });
  }

  applyFilters() {
    const term = this.searchTerm().toLowerCase();

    const filterPredicate = (s: ParkingSession) => {
        const matchesVehicle = s.vehicleNumber.toLowerCase().includes(term);
        return matchesVehicle;
    };

    // Note: Since the backend doesn't filter by text/date, we do it frontend side
    // on the already fetched arrays to keep things snappy.
    this.activeDataSource.filterPredicate = filterPredicate;
    this.activeDataSource.filter = 'trigger'; // dummy value to trigger filter

    this.historyDataSource.filterPredicate = filterPredicate;
    this.historyDataSource.filter = 'trigger';
  }

  onTerminate(session: any){
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '350px',
      data: {
        title: 'Confirm Termination',
        message: `Terminate ${session.vehicleNumber} ?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.performTerminate(session);
      }
    });
  }

  performTerminate(session: any) {
    this.parkingService.terminateSession(session.sessionId).subscribe({
      next: () => {
        this.notificationService.showSuccess("Session Terminated Successfully");
        this.loadData();
      },
      error: (err) => {
        const msg = err.error?.message || "Failed to terminate session";
        this.notificationService.showError(msg);
      }
    });
  }

  resetFilters() {
      this.searchTerm.set('');
      this.selectedLotId.set(null);
      this.loadData();
  }
}