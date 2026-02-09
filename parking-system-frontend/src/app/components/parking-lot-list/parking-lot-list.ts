import { Component, OnInit, NgZone, signal, viewChild, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ParkingService } from '../../services/parking';
import { NotificationService } from '../../services/notification';
import { ParkingLot } from '../../models/models.interface';
import { PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import {MatSort, Sort, MatSortModule} from '@angular/material/sort';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ConfirmDialog } from '../shared/confirm-dialog';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

/**
 * Parking Lot List Component
 * -------------------------------------------------------------------------
 * This is the Admin Dashboard.
 * Capabilities:
 * 1. Create new Parking Lots (POST).
 * 2. View all Parking Lots and their stats (GET).
 */

@Component({
  selector: 'app-parking-lot-list',
  imports: [FormsModule, 
    CommonModule, 
    MatTableModule, 
    MatPaginatorModule, 
    MatButtonModule, 
    MatInputModule, 
    MatFormFieldModule, 
    MatIconModule,
    MatCardModule,
    MatSortModule,
    MatSnackBarModule,
    MatDialogModule
  ],
  templateUrl: './parking-lot-list.html',
  styleUrl: '../../app.css',
})
export class ParkingLotList implements OnInit {

   // 1. Data Source and Paginator
  dataSource = new MatTableDataSource<ParkingLot>([]);
  displayedColumns: string[] = ['id', 'name', 'location', 'availability', 'price', 'actions'];

  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  // List of lots fetched from serve
  lots = signal<ParkingLot[]>([])

  //State for Editing
  isEditing = signal<boolean>(false);
  editingId = signal<number | null>(null);

  //DTO for Creating a Lot
  newLot = {
    name: '', 
    location: '', 
    totalSlots: null as any, 
    basePricePerHour: null as any
  };

  constructor(
    private parkingService: ParkingService, 
    private notificationService: NotificationService,
    private dialog: MatDialog, 
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
     if (isPlatformBrowser(this.platformId)){this.loadLots();}
  }

  // Fetches the latest list of lots from the API. 
  loadLots(){
    this.parkingService.getAllLots().subscribe(data => {
      this.lots.set(data)
      this.dataSource.data = data;
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    } );
  }

  createLot(){
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '350px',
      data: {
        title: 'Confirm Parking Lot Entry',
        message: `Are you sure you want to add this Parking Lot?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.performLot();
      }
    });
  }

  // Handles creation of a new parking lot.
  performLot(){

    this.parkingService.createLot(this.newLot).subscribe({
      next: () =>{

        // 1. Refresh the table data
        this.loadLots();

        // 2. Reset the form
        this.newLot = { name: '', location: '', totalSlots: null as any, basePricePerHour: null as any };

        // 3. Updates error message if Lot is added Succesfully.
        this.notificationService.showSuccess("Parking Lot Created Successfully");
      },
      error: (err) =>{
          console.error("Backend Error:", err);
        if (err.error && err.error.message) {
          this.notificationService.showSuccess(err.error.message);
        } else {
          this.notificationService.showSuccess("Failed to create lot. Check inputs.");
        }
      }
    });
  }

  // 1. Called when you click "Edit" on a table row
  startEdit(lot: ParkingLot) {
    this.isEditing.set(true);      // Turn on Edit Mode
    this.editingId.set(lot.id);    // Remember which ID we are editing

    // Fill the form with that lot's data
    this.newLot = {
      name: lot.name,
      location: lot.location,
      totalSlots: lot.totalSlots,
      basePricePerHour: lot.basePricePerHour
    };
  }

  updateCurrentLot(){
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '350px',
      data: {
        title: 'Update Parking Lot',
        message: `Are you sure you want to Update this changes?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.updateLot();
      }
    });

  }

  // 2. Called when you click "Update Lot" button
  updateLot() {

    const id = this.editingId();
    if (!id) return; // Safety check
    
    // Call the new Service method
    this.parkingService.updateLot(id, this.newLot).subscribe({
      next: () => {
        this.loadLots();    // Refresh list
        this.cancelEdit();  // Reset form to normal
        this.notificationService.showSuccess("Lot Updated Successfully");
      },
      error: (err) => {
        this.notificationService.showSuccess(err.error?.message || "Update Failed");
      }
    });
  }

  // 3. Called when you click "Cancel"
  cancelEdit() {
    this.isEditing.set(false);
    this.editingId.set(null);
    // Clear form
    this.newLot = { name: '', location: '', totalSlots: null as any, basePricePerHour: null as any };
  }

}
