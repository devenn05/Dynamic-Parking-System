import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ParkingService } from '../../services/parking';
import { EntryRequest, ExitRequest, ParkingTicket, Bill, ParkingLot } from '../../models/models.interface';
import { PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
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

  lots = signal<ParkingLot[]>([]);
  hasLots = signal<boolean>(true);
  
  entryData: EntryRequest = { vehicleNumber: '', vehicleType: 'CAR', parkingLotId: 0 };
  exitData: ExitRequest = { vehicleNumber: '' };
  
  bill = signal<Bill | null>(null);
  ticket = signal<ParkingTicket | null>(null);

  private messageTimer: any;

  constructor(
    private parkingService: ParkingService,
    private notificationService: NotificationService,
    private dialog: MatDialog, 
    @Inject(PLATFORM_ID) private platformId: Object) {}
  
  ngOnInit(): void {
     if (isPlatformBrowser(this.platformId)){this.loadLots();} 
  }

  loadLots(): void {
    this.parkingService.getAllLots().subscribe(data => {
      this.lots.set(data);
      this.hasLots.set(data.length > 0);
      if (this.lots().length > 0 && !this.entryData.parkingLotId) {
        this.entryData.parkingLotId = data[0].id;
      }
    });
  }

  clearMessages() {
    if (this.messageTimer) clearTimeout(this.messageTimer);
    this.ticket.set(null);
    this.bill.set(null);
  }

  handleEntry(form: NgForm){
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '350px',
      data: {
        title: 'Confirm Entry',
        message: `Generate ticket for ${this.entryData.vehicleNumber} (${this.entryData.vehicleType}) in Lot ${this.entryData.parkingLotId}?`
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

    this.parkingService.entryVehicle(this.entryData).subscribe({
      next: (res) => {
        this.ticket.set(res);
        this.loadLots();
        this.notificationService.showSuccess('Entry Approved: ${res.slotNumber} assigned');

        // Use resetForm to clear validation states (red text)
        form.resetForm({
          vType: this.entryData.vehicleType,
          pLot: this.entryData.parkingLotId,
          vNum: ''
        });

        this.messageTimer = setTimeout(() => this.ticket.set(null), 7000); 
      },
      error: (err) => {
        this.notificationService.showError(err.error?.message || 'Error Occurred');
      }
    });
  }

  handleExit(form: NgForm) {
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

    this.parkingService.exitVehicle(this.exitData).subscribe({
      next: (res) => {
        this.bill.set(res);
        this.loadLots();
        this.notificationService.showSuccess("Exit Vehicle Successful.")
        
        // Fully reset the exit form state
        form.resetForm();

        this.messageTimer = setTimeout(() => this.bill.set(null), 15000); 
      },
      error: (err) => {
        this.notificationService.showError(err.error?.message || 'Error Occurred');
      }
    });
  }
  
  // Format input value while preserving Angular model state
  formatVehicleNumber(type: 'ENTRY' | 'EXIT') {
    if (type === 'ENTRY') {
      this.entryData.vehicleNumber = this.entryData.vehicleNumber.toUpperCase().replace(/[^A-Z0-9]/g, '');
    } else {
      this.exitData.vehicleNumber = this.exitData.vehicleNumber.toUpperCase().replace(/[^A-Z0-9]/g, '');
    }
  }

  get vehiclePattern(): string {
    // Regex for Standard and BH series
    return "^([A-Z]{2}[0-9]{2}[A-Z]{0,3}[0-9]{4}|[0-9]{2}BH[0-9]{4}[A-Z]{1,2})$";
  }
}