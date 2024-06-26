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
import { PicvidService } from '../../services/picvid.service';

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

  dateFormat: string = 'dd/MM/yyyy HH:mm';

  constructor(
    private _adapter: DateAdapter<any>,
    @Inject(MAT_DATE_LOCALE) private _locale: string,
    private fb: FormBuilder,
    private picvidService: PicvidService
  ) {}

  ngOnInit(): void {
    if (window.location.href.includes('/en/')) {
      this.dateFormat = 'MM/dd/yyyy h:mm a';
      this._locale = 'en';
      this._adapter.setLocale(this._locale);
    } else {
      this._locale = 'fr';
      this._adapter.setLocale(this._locale);
    }
    this.reinitForm();
  }

  reinitForm(): void {
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
    const formData = new FormData();
    formData.append('name', this.form.get('name')!.value);
    formData.append('description', this.form.get('description')!.value);
    formData.append('takenLocation', this.form.get('takenLocation')!.value);

    if (
      this.form.get('date') &&
      this.form.get('date')!.value != null &&
      this.form.get('date')!.value != ''
    ) {
      formData.append('date', this.form.get('date')!.value.toDate());
    } else {
      formData.append('isDateDeleted', 'true');
    }

    this.picvidService
      .modifyPicvid(this.picvid.album_id, this.picvid.id, formData)
      .subscribe({
        next: (resp) => {
          this.picvid = resp;
          this.reinitForm();
        },
      });
  }
}
