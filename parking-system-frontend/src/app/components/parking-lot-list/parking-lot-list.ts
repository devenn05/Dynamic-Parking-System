import { Component, OnInit, NgZone, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ParkingService } from '../../services/parking';
import { ParkingLot } from '../../models/models.interface';

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
  imports: [FormsModule, CommonModule],
  templateUrl: './parking-lot-list.html',
  styleUrl: '../../app.css',
})
export class ParkingLotList implements OnInit {

  // List of lots fetched from serve
  lots = signal<ParkingLot[]>([])

  errorMessage = signal<string>("")

  //DTO for Creating a Lot
  newLot = {
    name: '', 
    location: '', 
    totalSlots: 10, 
    basePricePerHour: 10
  };

  constructor(private parkingService: ParkingService) {}

  ngOnInit(): void {
    this.loadLots();
  }

  // Fetches the latest list of lots from the API. 
  loadLots(){
    this.parkingService.getAllLots().subscribe(data => this.lots.set(data));
  }

  // Handles creation of a new parking lot.
  createLot(){
    this.errorMessage.set("")
    this.parkingService.createLot(this.newLot).subscribe({
      next: () =>{

        // 1. Refresh the table data
        this.loadLots();

        // 2. Reset the form
        this.newLot = { name: '', location: '', totalSlots: 10, basePricePerHour: 10 };

        // 3. Updates error message if Lot is added Succesfully.
        this.errorMessage.set("Lot added Successfully")
      },
      error: (err) =>{
          console.error("Backend Error:", err);
        if (err.error && err.error.message) {
            this.errorMessage.set(err.error.message);
        } else {
            this.errorMessage.set("Failed to create lot. Check inputs.");
        }
      }
    });
  }

}
