import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { ConfirmDialogComponent } from '../components/confirm-dialog/confirm-dialog.component';
import { InputDialogComponent } from '../components/input-dialog/input-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  constructor(private dialog: MatDialog) {}

  openConfirmDialog(message: string, showCancel: boolean): Observable<boolean> {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        message,
        showCancel,
      },
    });

    return dialogRef.afterClosed();
  }

  openInputDialog(message: string): Observable<string | undefined> {
    const dialogRef = this.dialog.open(InputDialogComponent, {
      data: {
        message,
      },
    });

    return dialogRef.afterClosed();
  }
}
