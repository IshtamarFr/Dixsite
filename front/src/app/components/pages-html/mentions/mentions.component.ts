import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AppSettings } from '../../../utils/app-settings';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-mentions',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, CommonModule],
  templateUrl: './mentions.component.html',
  styleUrl: './mentions.component.scss',
})
export class MentionsComponent implements OnInit {
  CONTACT_EMAIL = AppSettings.CONTACT_EMAIL;
  public english: boolean = false;

  constructor(private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.english = this.activatedRoute.snapshot.url.toString().includes('/en/');
  }

  public back() {
    window.history.back();
  }
}
