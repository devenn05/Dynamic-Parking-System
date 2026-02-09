import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) { }

  showSuccess(message: string): void {
    this.snackBar.open(message, 'OK', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['snackbar-success', 'snackbar-large']
    });
  }

  showError(message: string): void {
    this.snackBar.open(message, 'CLOSE', {
      duration: 7000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['snackbar-error', 'snackbar-large']
    });
  }
}