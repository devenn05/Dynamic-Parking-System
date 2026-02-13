import { Component, OnInit, OnDestroy, signal } from '@angular/core';
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
import { Subscription } from 'rxjs';
import { RealTimeService } from '../../services/real-time-service';

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatIconModule],
  templateUrl: './home.html'
})
export class HomeComponent implements OnInit, OnDestroy {

  private lotSub: Subscription | null = null;
  lots = signal<ParkingLot[]>([]);
  selectedLotId: number | null = null;

  constructor(
    private parkingService: ParkingService, 
    private router: Router,
    private realTimeService: RealTimeService
  ) {}

  ngOnInit() {
    // 1. Initial Load
    this.parkingService.getAllLots().subscribe(data => this.lots.set(data));

    // 2. Real-Time Listen
    this.lotSub = this.realTimeService.getGlobalLotUpdates().subscribe(update => {
      if (update.type === 'LOT_CREATED') {
        this.lots.set([...this.lots(), update.lot]);
      } else if (update.type === 'LOT_DELETED') {
        this.lots.set(this.lots().filter(l => l.id !== update.lot.id));
      } else if (update.type === 'LOT_UPDATED') {
        const updatedList = this.lots().map(l => l.id === update.lot.id ? update.lot : l);
        this.lots.set(updatedList);
      }
    });
  }

  ngOnDestroy() { this.lotSub?.unsubscribe(); }

  enterLot() {
    if (this.selectedLotId) {
      // Navigate to the Dashboard Layout
      this.router.navigate(['/lot', this.selectedLotId, 'operations']);
    }
  }
}