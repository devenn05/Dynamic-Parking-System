import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export interface DialogData {
  title: string;
  message: string;
}

@Component({
  selector: 'app-confirm-dialog',
  imports: [MatDialogModule, MatButtonModule],
  standalone: true,
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <p>{{ data.message }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <!-- "false" is returned if clicked Cancel -->
      <button mat-button [mat-dialog-close]="false">Cancel</button>
      
      <!-- "true" is returned if clicked Confirm -->
      <button mat-flat-button color="warn" [mat-dialog-close]="true">Confirm</button>
    </mat-dialog-actions>
  `
})
export class ConfirmDialog {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {}
}