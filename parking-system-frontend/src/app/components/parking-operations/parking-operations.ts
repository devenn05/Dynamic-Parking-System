import { Component, OnInit, signal  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ParkingService } from '../../services/parking';
import { EntryRequest, ExitRequest, ParkingTicket, Bill, ParkingLot } from '../../models/models.interface';

/**
 * Parking Operations Component
 * -------------------------------------------------------------------------
 * This is the main "User Interface" for the gatekeeper.
 * It handles the two primary transactional workflows:
 * 1. Vehicle Entry -> Generates a Ticket.
 * 2. Vehicle Exit -> Generates a Bill.
 */

@Component({
  selector: 'app-parking-operations',
  imports: [CommonModule, FormsModule],
  templateUrl: './parking-operations.html',
  styleUrl: '../../app.css',
})
export class ParkingOperations implements OnInit {

   // To store available parking lots for the dropdown
  lots = signal<ParkingLot[]>([]);

  // State signal to check if we even have lots to show
  hasLots = signal<boolean>(true);

  constructor(private parkingService: ParkingService) {}
  
  // DTO for Entry Form Binding
  entryData: EntryRequest = {vehicleNumber: '', 
                  vehicleType: 'CAR',  
                  parkingLotId: 1
                };
  
  // DTO for Exit Form Binding
  exitData: ExitRequest = {vehicleNumber: ''};
  
  // Response signals to display results UI
  bill = signal<Bill | null>(null)
  ticket = signal<ParkingTicket | null>(null)
  errorMessage = signal<string>("")
  
  //  On Initializationit fetch all parking lots so we can populate the "Select Lot" dropdown for entry form.
  ngOnInit(): void {
    this.parkingService.getAllLots().subscribe(data=>{
      this.lots.set(data);
      this.hasLots.set(data.length > 0);

      // Auto-select the first lot if available
      if (this.lots().length > 0) this.entryData.parkingLotId = data[0].id;
    })
  }

  // Handles the 'Park Vehicle' form submission.
  handleEntry(){
    this.ticket.set(null);
    this.errorMessage.set("");
    this.parkingService.entryVehicle(this.entryData).subscribe({
      next: (res) => this.ticket.set(res),
      error: (err) => this.errorMessage.set(err.error?.message || 'Error Occured')
    });
    
  }

  //Handles the 'Exit Vehicle' form submission.
  handleExit(){
    this.bill.set(null)
    this.errorMessage.set("")
    this.parkingService.exitVehicle(this.exitData).subscribe({
      next: (res) =>this.bill.set(res),
      error: (err) => this.errorMessage.set(err.error?.message || 'Error Occured')
    });
  }
}
