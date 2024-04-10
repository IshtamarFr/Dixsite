import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AppSettings } from '../../../utils/app-settings';

@Component({
  selector: 'app-mentions',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, CommonModule],
  templateUrl: './mentions.component.html',
  styleUrl: './mentions.component.scss',
})
export class MentionsComponent {
  CONTACT_EMAIL = AppSettings.CONTACT_EMAIL;

  public back() {
    window.history.back();
  }
}
