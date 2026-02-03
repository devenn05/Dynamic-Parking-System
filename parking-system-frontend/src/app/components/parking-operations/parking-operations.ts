import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ParkingService } from '../../services/parking';
import { EntryRequest, ExitRequest, ParkingTicket, Bill, ParkingLot } from '../../models/models.interface';
import { PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-parking-operations',
  imports: [CommonModule, FormsModule],
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
  errorMessage = signal<string>("");

  private messageTimer: any;

  constructor(private parkingService: ParkingService, @Inject(PLATFORM_ID) private platformId: Object) {}
  
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
    this.errorMessage.set("");
  }

  handleEntry(form: NgForm) {
    if (!confirm("Confirm vehicle Entry?")) return;
    this.clearMessages(); 

    this.parkingService.entryVehicle(this.entryData).subscribe({
      next: (res) => {
        this.ticket.set(res);
        this.loadLots();

        // Use resetForm to clear validation states (red text)
        form.resetForm({
          vType: this.entryData.vehicleType,
          pLot: this.entryData.parkingLotId,
          vNum: ''
        });

        this.messageTimer = setTimeout(() => this.ticket.set(null), 7000); 
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Error Occurred');
      }
    });
  }

  handleExit(form: NgForm) {
    if (!confirm("Confirm vehicle Exit?")) return;
    this.clearMessages();

    this.parkingService.exitVehicle(this.exitData).subscribe({
      next: (res) => {
        this.bill.set(res);
        this.loadLots();
        
        // Fully reset the exit form state
        form.resetForm();

        this.messageTimer = setTimeout(() => this.bill.set(null), 15000); 
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Error Occurred');
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
    return "^([A-Z]{2}[0-9]{2}[A-Z]{1,3}[0-9]{4}|[0-9]{2}BH[0-9]{4}[A-Z]{1,2})$";
  }
}