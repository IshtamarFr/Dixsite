import { CommonModule } from '@angular/common';
import { Component, Inject, Input, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { RouterModule } from '@angular/router';
import { Picvid } from '../../interfaces/picvid.interface';
import { MatFormFieldModule } from '@angular/material/form-field';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-picvid-title',
  standalone: true,
  imports: [
    MatCardModule,
    RouterModule,
    CommonModule,
    MatFormFieldModule,
    MatDatepickerModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
  ],
  providers: [
    MatDatepickerModule,
    { provide: MAT_DATE_LOCALE, useValue: 'fr' },
    provideMomentDateAdapter(),
  ],
  templateUrl: './picvid-title.component.html',
  styleUrl: './picvid-title.component.scss',
})
export class PicvidTitleComponent implements OnInit {
  @Input() picvid!: Picvid;
  @Input() isAdminTriggered: boolean = false;

  public form!: FormGroup;

  constructor(
    private _adapter: DateAdapter<any>,
    @Inject(MAT_DATE_LOCALE) private _locale: string,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this._locale = 'fr';
    this._adapter.setLocale(this._locale);

    this.form = this.fb.group({
      name: ['', [Validators.minLength(3), Validators.maxLength(63)]],
      description: ['', [Validators.minLength(3), Validators.maxLength(255)]],
      takenLocation: ['', [Validators.minLength(3), Validators.maxLength(63)]],
      date: ['', [Validators.minLength(3), Validators.maxLength(255)]],
    });
  }

  bestDate(picvid: Picvid): Date {
    if (picvid.dateTime) {
      return picvid.dateTime;
    } else if (picvid.dateTimeExif) {
      return picvid.dateTimeExif;
    } else {
      return picvid.createdAt;
    }
  }

  submit(): void {
    window.alert('WIP');
  }
}
