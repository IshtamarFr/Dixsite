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
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';

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
    MatDividerModule,
    MatTooltipModule,
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
      name: [
        this.picvid.name,
        [Validators.minLength(3), Validators.maxLength(63)],
      ],
      description: [
        this.picvid.description,
        [Validators.minLength(3), Validators.maxLength(255)],
      ],
      takenLocation: [
        this.picvid.takenLocation,
        [Validators.minLength(3), Validators.maxLength(63)],
      ],
      date: [
        this.picvid.dateTime,
        [Validators.minLength(3), Validators.maxLength(255)],
      ],
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
