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

    // Confirmation Message box 
    if(!confirm("Confirm Lot entry?")) return;

    this.errorMessage.set("")
    this.parkingService.createLot(this.newLot).subscribe({
      next: () =>{

        // 1. Refresh the table data
        this.loadLots();

        // 2. Reset the form
        this.newLot = { name: '', location: '', totalSlots: null as any, basePricePerHour: null as any };

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

  // 2. Called when you click "Update Lot" button
  updateCurrentLot() {
    if(!confirm("Confirm changes to this lot?")) return;

    const id = this.editingId();
    if (!id) return; // Safety check

    this.errorMessage.set("");
    
    // Call the new Service method
    this.parkingService.updateLot(id, this.newLot).subscribe({
      next: () => {
        this.loadLots();    // Refresh list
        this.cancelEdit();  // Reset form to normal
        this.errorMessage.set("Lot Updated Successfully");
      },
      error: (err) => {
         this.errorMessage.set(err.error?.message || "Update Failed");
      }
    });
  }

  // 3. Called when you click "Cancel"
  cancelEdit() {
    this.isEditing.set(false);
    this.editingId.set(null);
    // Clear form
    this.newLot = { name: '', location: '', totalSlots: null as any, basePricePerHour: null as any };
    this.errorMessage.set("");
  }

}
