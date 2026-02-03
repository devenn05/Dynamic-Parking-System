import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ParkingService } from '../../services/parking';
import { EntryRequest, ExitRequest, ParkingTicket, Bill, ParkingLot } from '../../models/models.interface';

@Component({
  selector: 'app-parking-operations',
  imports: [CommonModule, FormsModule],
  templateUrl: './parking-operations.html',
  styleUrl: '../../app.css',
})
export class ParkingOperations implements OnInit {

  lots = signal<ParkingLot[]>([]);
  hasLots = signal<boolean>(true);
  
  // DTOs for Form Binding
  entryData: EntryRequest = { vehicleNumber: '', vehicleType: 'CAR', parkingLotId: 1 };
  exitData: ExitRequest = { vehicleNumber: '' };
  
  // Response signals
  bill = signal<Bill | null>(null);
  ticket = signal<ParkingTicket | null>(null);
  errorMessage = signal<string>("");

  // We will store the timer ID to prevent multiple timers running
  private messageTimer: any;

  constructor(private parkingService: ParkingService) {}
  
  ngOnInit(): void {
    this.loadLots(); 
  }

  // Reusable method to load lot data
  loadLots(): void {
    this.parkingService.getAllLots().subscribe(data => {
      this.lots.set(data);
      this.hasLots.set(data.length > 0);
      
      // Auto-select the first lot if it exists and wasn't manually chosen
      if (this.lots().length > 0 && !this.entryData.parkingLotId) {
        this.entryData.parkingLotId = data[0].id;
      }
    });
  }

  // Central method to clear all messages
  clearMessages() {
    // Clear any pending timer
    if (this.messageTimer) {
      clearTimeout(this.messageTimer);
    }
    this.ticket.set(null);
    this.bill.set(null);
    this.errorMessage.set("");
  }

  handleEntry() {
    if (!confirm("Confirm vehicle Entry?")) return;
    
    // Clear previous results immediately on new action
    this.clearMessages(); 

    this.parkingService.entryVehicle(this.entryData).subscribe({
      next: (res) => {
        this.ticket.set(res);
        this.loadLots(); // <-- NEW: Refresh lot data immediately!
        
        // Reset the form for the next entry
        this.entryData.vehicleNumber = '';

        // Set a timer to automatically clear the ticket after 7 seconds
        this.messageTimer = setTimeout(() => this.ticket.set(null), 7000); 
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Error Occurred');
        // Set a timer to automatically clear the error after 7 seconds
        this.messageTimer = setTimeout(() => this.errorMessage.set(""), 7000);
      }
    });
  }

  handleExit() {
    if (!confirm("Confirm vehicle Exit?")) return;
    
    // Clear previous results immediately on new action
    this.clearMessages();

    this.parkingService.exitVehicle(this.exitData).subscribe({
      next: (res) => {
        this.bill.set(res);
        this.loadLots(); // <-- NEW: Refresh lot data immediately!

        // Reset the form for the next exit
        this.exitData.vehicleNumber = '';

        // Set a timer to automatically clear the bill after 15 seconds
        this.messageTimer = setTimeout(() => this.bill.set(null), 15000); 
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Error Occurred');
        // Set a timer to automatically clear the error after 7 seconds
        this.messageTimer = setTimeout(() => this.errorMessage.set(""), 7000);
      }
    });
  }
  
  // Sanitizer and Regex getter remain the same...
  formatVehicleNumber(event: any, type: 'ENTRY' | 'EXIT') {
    let val = event.target.value.toUpperCase().replace(/[^A-Z0-9]/g, '');
    if (type === 'ENTRY') {
      this.entryData.vehicleNumber = val;
    } else {
      this.exitData.vehicleNumber = val;
    }
    event.target.value = val; 
  }

  get vehiclePattern(): string {
    return "^([A-Z]{2}[0-9]{2}[A-Z]{1,3}[0-9]{4}|[0-9]{2}BH[0-9]{4}[A-Z]{1,2})$";
  }
}