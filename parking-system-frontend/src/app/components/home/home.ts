import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ParkingService } from '../../services/parking';
import { ParkingLot } from '../../models/models.interface';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatIconModule],
  templateUrl: './home.html'
})
export class HomeComponent implements OnInit {
  lots = signal<ParkingLot[]>([]);
  selectedLotId: number | null = null;

  constructor(private parkingService: ParkingService, private router: Router) {}

  ngOnInit() {
    this.parkingService.getAllLots().subscribe(data => this.lots.set(data));
  }

  enterLot() {
    if (this.selectedLotId) {
      // Navigate to the Dashboard Layout
      this.router.navigate(['/lot', this.selectedLotId, 'operations']);
    }
  }
}