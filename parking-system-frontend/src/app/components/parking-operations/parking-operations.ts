import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router'; // <--- NEW IMPORT
import { ParkingService } from '../../services/parking';
import { EntryRequest, ExitRequest, ParkingTicket, Bill, ParkingLot } from '../../models/models.interface';
import { NotificationService } from '../../services/notification';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ConfirmDialog } from '../shared/confirm-dialog';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-parking-operations',
  imports: [CommonModule, FormsModule, MatSnackBarModule, MatDialogModule],
  templateUrl: './parking-operations.html',
  styleUrl: '../../app.css',
})
export class ParkingOperations implements OnInit {

  // We no longer need a list of 'lots'. We just need the details of the CURRENT lot.
  currentLot = signal<ParkingLot | null>(null);
  currentLotId: number = 0; 
  
  entryData: EntryRequest = { vehicleNumber: '', vehicleType: 'CAR', parkingLotId: 0 };
  exitData: ExitRequest = { vehicleNumber: '', parkingLotId: 0 };
  
  bill = signal<Bill | null>(null);
  ticket = signal<ParkingTicket | null>(null);
  private messageTimer: any;

  constructor(
    private parkingService: ParkingService,
    private notificationService: NotificationService,
    private dialog: MatDialog,
    private route: ActivatedRoute
  ) {}
  
  ngOnInit(): void {
    // Get ID from the Parent Route (/lot/:id/operations)
    this.route.parent?.params.subscribe(params => {
       this.currentLotId = +params['id']; 
       this.entryData.parkingLotId = this.currentLotId;
       this.exitData.parkingLotId = this.currentLotId; 
    });
  }

  // <--- NEW: Only fetch info for THIS lot to see availability
  loadCurrentLotStatus(): void {
    this.parkingService.getParkingLotById(this.currentLotId).subscribe({
      next: (lot) => {
        this.currentLot.set(lot);
      },
      error: () => this.notificationService.showError("Failed to load lot details")
    });
  }

  clearMessages() {
    if (this.messageTimer) clearTimeout(this.messageTimer);
    this.ticket.set(null);
    this.bill.set(null);
  }

  handleEntry(form: NgForm){
    // Validation check for full lot
    if (this.currentLot() && this.currentLot()!.availableSlots === 0) {
      this.notificationService.showError("This lot is full!");
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '350px',
      data: {
        title: 'Confirm Entry',
        message: `Generate ticket for ${this.entryData.vehicleNumber}?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.performEntry(form);
      }
    });
  }

  private performEntry(form: NgForm) {
    this.clearMessages(); 

    // entryData.parkingLotId is already set in ngOnInit
    this.parkingService.entryVehicle(this.entryData).subscribe({
      next: (res) => {
        this.ticket.set(res);
        this.loadCurrentLotStatus(); // Refresh slot count
        this.notificationService.showSuccess(`Entry Approved: ${res.slotNumber} assigned`);

        form.resetForm({
          vType: 'CAR',
          vNum: ''
        });
        
        // RE-ASSIGN the ID after reset because resetForm wipes it
        this.entryData.parkingLotId = this.currentLotId; 
        this.entryData.vehicleType = 'CAR';

        this.messageTimer = setTimeout(() => this.ticket.set(null), 7000); 
      },
      error: (err) => {
        this.notificationService.showError(err.error?.message || 'Error Occurred');
      }
    });
  }

  handleExit(form: NgForm) {
    // ... exit logic remains mostly the same ...
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '350px',
      data: {
        title: 'Confirm Exit',
        message: `Generate bill and exit vehicle ${this.exitData.vehicleNumber}?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.performExit(form);
      }
    });
  }

  private performExit(form: NgForm) {
    this.clearMessages();
    this.exitData.parkingLotId = this.currentLotId; 
    this.parkingService.exitVehicle(this.exitData).subscribe({
      next: (res) => {
        this.bill.set(res);
        this.loadCurrentLotStatus(); // Refresh slot count
        this.notificationService.showSuccess("Exit Vehicle Successful.")
        
        form.resetForm();

        this.messageTimer = setTimeout(() => this.bill.set(null), 15000); 
      },
      error: (err) => {
        this.notificationService.showError(err.error?.message || 'Error Occurred');
      }
    });
  }
  
  formatVehicleNumber(type: 'ENTRY' | 'EXIT') {
    if (type === 'ENTRY') {
      this.entryData.vehicleNumber = this.entryData.vehicleNumber.toUpperCase().replace(/[^A-Z0-9]/g, '');
    } else {
      this.exitData.vehicleNumber = this.exitData.vehicleNumber.toUpperCase().replace(/[^A-Z0-9]/g, '');
    }
  }

  get vehiclePattern(): string {
    return "^([A-Z]{2}[0-9]{2}[A-Z]{0,3}[0-9]{4}|[0-9]{2}BH[0-9]{4}[A-Z]{1,2})$";
  }
}