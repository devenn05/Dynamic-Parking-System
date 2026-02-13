import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterOutlet, RouterLink, RouterLinkActive, Router  } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs'; // Using tabs for navigation visuals
import { ParkingService } from '../../services/parking';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule, NgForm } from '@angular/forms';
import { ConfirmDialog } from '../shared/confirm-dialog';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-lot-layout',
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, MatTabsModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './lot-layout.html',
  styleUrl: './lot-layout.css',
  styles: [`
    
  `]
})
export class LotLayoutComponent implements OnInit {
  lotId: number = 0;
  lotName: string = 'Loading...';

  constructor(private route: ActivatedRoute, 
    private parkingService: ParkingService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    // 1. Get ID from URL
    this.route.params.subscribe(params => {
        this.lotId = +params['id']; // (+) converts string 'id' to a number
        this.loadLotDetails();
    });
  }

  loadLotDetails() {
      // Optimization: You might want to create a specific getLotById endpoint 
      // or just filter from the full list if the list is small. 
      // For now, let's just fetch single lot.
      this.parkingService.getAllLots().subscribe(lots => {
          const lot = lots.find(l => l.id === this.lotId);
          if (lot) this.lotName = lot.name;
      });
  }

  handleExit() {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '350px',
      data: {
        title: 'Confirm Exit',
        message: `Are you sure you want to leave ${this.lotName}?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.router.navigate(['/']); 
      }
    });
  }
}