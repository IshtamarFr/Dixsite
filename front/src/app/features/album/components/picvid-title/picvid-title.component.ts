import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { RouterModule } from '@angular/router';
import { Picvid } from '../../interfaces/picvid.interface';

@Component({
  selector: 'app-picvid-title',
  standalone: true,
  imports: [MatCardModule, RouterModule, CommonModule],
  templateUrl: './picvid-title.component.html',
  styleUrl: './picvid-title.component.scss',
})
export class PicvidTitleComponent {
  @Input() picvid!: Picvid;

  bestDate(picvid: Picvid): Date {
    if (picvid.dateTime) {
      return picvid.dateTime;
    } else if (picvid.dateTimeExif) {
      return picvid.dateTimeExif;
    } else {
      return picvid.createdAt;
    }
  }
}
