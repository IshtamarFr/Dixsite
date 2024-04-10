import { Component } from '@angular/core';
import { AppSettings } from '../../utils/app-settings';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss',
})
export class FooterComponent {
  CONTACT_EMAIL = AppSettings.CONTACT_EMAIL;
}
