import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppSettings } from '../../utils/app-settings';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-input-dialog',
  standalone: true,
  imports: [
    MatButtonModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
  ],
  templateUrl: './input-dialog.component.html',
  styleUrl: './input-dialog.component.scss',
})
export class InputDialogComponent implements OnInit {
  message: string =
    'missing message, please contact us at ' + AppSettings.CONTACT_EMAIL;
  showCancel: boolean = true;
  public form!: FormGroup;

  constructor(
    private dialogRef: MatDialogRef<ConfirmDialogComponent>,
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) private data: any
  ) {}

  ngOnInit() {
    this.message = this.data.message;

    this.form = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(512)]],
    });
  }

  onConfirm(): void {
    const result: string = this.form.get('content')!.value;
    this.dialogRef.close(result);
  }

  onCancel(): void {
    this.dialogRef.close(undefined);
  }
}
