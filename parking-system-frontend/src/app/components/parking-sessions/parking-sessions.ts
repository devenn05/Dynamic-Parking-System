import { Component, OnInit, signal, ViewChild, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router'; // <--- NEW IMPORT
import { ParkingService } from '../../services/parking';
import { ParkingSession } from '../../models/models.interface';
import { ConfirmDialog } from '../shared/confirm-dialog';

// Material Imports
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select'; // Might remove this if no other selects exist
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import {MatSort, MatSortModule} from '@angular/material/sort';
import { NotificationService } from '../../services/notification';
import { MatSnackBarModule } from '@angular/material/snack-bar'; 
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-parking-sessions',
  imports: [
    CommonModule, FormsModule, MatTableModule, MatPaginatorModule, 
    MatTabsModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatCardModule, MatSortModule,
    MatSnackBarModule, MatDialogModule
  ],
  templateUrl: './parking-sessions.html',
  styleUrl: '../../app.css',
})
export class ParkingSessions implements OnInit {
  activeDataSource = new MatTableDataSource<ParkingSession>([]);
  historyDataSource = new MatTableDataSource<ParkingSession>([]);

  activeColumns: string[] = ['vehicleNumber', 'vehicleType', 'parkingLotName', 'slotNumber', 'entryTime', 'actions'];
  historyColumns: string[] = ['vehicleNumber', 'vehicleType', 'parkingLotName', 'slotNumber', 'entryTime', 'exitTime', 'totalAmount', 'status'];

  @ViewChild('activePaginator') activePaginator!: MatPaginator;
  @ViewChild('historyPaginator') historyPaginator!: MatPaginator;
  @ViewChild('activeSort') activeSort!: MatSort;
  @ViewChild('historySort') historySort!: MatSort;

  // REMOVED 'lots' signal. We don't need a dropdown list anymore.
  selectedLotId = signal<number | null>(null);
  searchTerm = signal<string>('');

  constructor(
    private parkingService: ParkingService,
    private notificationService: NotificationService, 
    private dialog: MatDialog,
    private route: ActivatedRoute // <--- INJECT ACTIVATED ROUTE
  ) {
    effect(() => {
        this.applyFilters();
    });
  }

  ngOnInit(): void {
    // <--- CRITICAL CHANGE: Get ID from URL, ignore everything else
    this.route.parent?.params.subscribe(params => {
       const id = +params['id'];
       this.selectedLotId.set(id); // Set the Lot ID
       this.loadData(); // Trigger the API call
    });
  }

  loadData() {
    const id = this.selectedLotId();
    if (!id) return;

    // We explicitly pass the ID from the URL, effectively filtering backend-side
    this.parkingService.getActiveSessions(id)
      .subscribe(data => {
          this.activeDataSource.data = data;
          this.activeDataSource.paginator = this.activePaginator;
          this.activeDataSource.sort = this.activeSort;
      });

    this.parkingService.getAllSessions(id)
      .subscribe(data => {
          this.historyDataSource.data = data;
          this.historyDataSource.paginator = this.historyPaginator;
          this.historyDataSource.sort = this.historySort;
      });
  }

  applyFilters() {
    // Simple Vehicle Text Search filter
    const term = this.searchTerm().toLowerCase();
    const filterPredicate = (s: ParkingSession) => s.vehicleNumber.toLowerCase().includes(term);

    this.activeDataSource.filterPredicate = filterPredicate;
    this.activeDataSource.filter = 'trigger'; 

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
      // <--- CHANGED: Just clear text, KEEP the lot ID
      this.searchTerm.set('');
      this.loadData();
  }
}